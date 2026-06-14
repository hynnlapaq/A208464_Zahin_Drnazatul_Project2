package com.example.a208464_zahin_drnazatul_project2

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ── UI states for API & Firestore ────────────────────────────────────────────
sealed class ApiState<out T> {
    object Idle    : ApiState<Nothing>()
    object Loading : ApiState<Nothing>()
    data class Success<T>(val data: T) : ApiState<T>()
    data class Error(val message: String) : ApiState<Nothing>()
}

class JobViewModel(
    private val repository:         ApplicationRepository,
    private val firestoreRepo:      FirestoreRepository   = FirestoreRepository(),
    private val countryRepository:  CountryRepository     = CountryRepository()
) : ViewModel() {

    // ── Firestore sync status ────────────────────────────────────────────────
    var firestoreStatus by mutableStateOf<String?>(null)
        private set

    // ── Room — local persistence ─────────────────────────────────────────────
    val applications: StateFlow<List<ApplicationEntity>> =
        repository.allApplications.stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5000),
            initialValue  = emptyList()
        )

    fun submitApplication(application: ApplicationEntity) {
        viewModelScope.launch {
            // 1. Save locally (Room) - This always works
            repository.insert(application)
            
            // 2. Mirror to Firestore (Cloud)
            firestoreStatus = "Uploading..."
            val result = firestoreRepo.uploadApplication(application)
            
            result.onSuccess {
                firestoreStatus = "Synced to Cloud ✓"
            }.onFailure { e ->
                // This captures the "Chain validation failed" error if the date is wrong
                firestoreStatus = "Sync Failed: ${e.localizedMessage}"
            }
        }
    }

    fun deleteApplication(application: ApplicationEntity) {
        viewModelScope.launch {
            repository.delete(application)
            firestoreRepo.deleteApplication(application)
        }
    }

    fun hasApplied(jobTitle: String): Boolean =
        applications.value.any { it.jobTitle == jobTitle }

    // ── Selected job ─────────────────────────────────────────────────────────
    var selectedJob by mutableStateOf<JobPost?>(null)
        private set

    fun onJobSelected(job: JobPost) { selectedJob = job }

    // ── REST Countries API state ─────────────────────────────────────────────
    var countryStatsState by mutableStateOf<ApiState<MalaysiaStats>>(ApiState.Idle)
        private set

    fun fetchMalaysiaStats() {
        if (countryStatsState is ApiState.Loading) return
        viewModelScope.launch {
            countryStatsState = ApiState.Loading
            val result = countryRepository.getMalaysiaStats()
            countryStatsState = result.fold(
                onSuccess = { ApiState.Success(it) },
                onFailure = { ApiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    // ── GPS / Location ──────────────────────────────────────────────────────
    var userLocation by mutableStateOf<Location?>(null)
        private set
    fun onLocationReceived(location: Location) { userLocation = location }

    var userCity by mutableStateOf<String?>(null)
        private set
    fun onCityResolved(city: String) { userCity = city }

    val nearbyJobs: List<JobPost> get() {
        val city = userCity ?: return jobs
        return jobs.filter { it.location.contains(city, ignoreCase = true) }.ifEmpty { jobs }
    }

    // ── Scanning / OCR ──────────────────────────────────────────────────────
    var scannedData by mutableStateOf<String?>(null)
        private set
    var scannedApplicantName  by mutableStateOf("")
    var scannedApplicantEmail by mutableStateOf("")
    var scannedApplicantId    by mutableStateOf("")

    fun onQrScanned(raw: String) {
        val name  = Regex("Name:([^|]+)").find(raw)?.groupValues?.get(1)?.trim()
        val email = Regex("Email:([^|]+)").find(raw)?.groupValues?.get(1)?.trim()
        scannedData = raw
        if (name  != null) scannedApplicantName  = name
        if (email != null) scannedApplicantEmail = email
    }

    fun onTextRecognized(text: String) {
        scannedData = text
        val icPattern = Regex("\\d{6}-\\d{2}-\\d{4}")
        val icMatch = icPattern.find(text)
        if (icMatch != null) scannedApplicantId = icMatch.value
        val lines = text.lines().filter { it.isNotBlank() }
        if (lines.isNotEmpty()) {
            val possibleName = lines.find { it.length > 5 && it == it.uppercase() && !it.contains("-") }
            if (possibleName != null) scannedApplicantName = possibleName
        }
    }

    fun clearScan() {
        scannedData = null
        scannedApplicantName = ""; scannedApplicantEmail = ""; scannedApplicantId = ""
    }

    // ── All job listings ──────────────────────────────────────────────────────
    val jobs = listOf(
        JobPost(1, "Junior Web Developer", "Tech Solutions Corp", "Kuala Lumpur", "RM 2,500 - 3,500", "Entry level tech position.", "Diploma in IT.", "Technology"),
        JobPost(2, "Customer Service Officer", "Amanah Finance", "Selangor", "RM 1,800 - 2,500", "Assist customers with queries.", "SPM minimum.", "Finance"),
        JobPost(3, "Data Entry Clerk", "Global Logistics Sdn Bhd", "Putrajaya", "RM 1,600 - 2,000", "Handle logistics data.", "Basic PC skills.", "Administration"),
        JobPost(4, "Production Operator", "ManuFlex Industries", "Johor Bahru", "RM 1,700 - 2,200", "Shift work available.", "Physically fit.", "Manufacturing"),
        JobPost(5, "Social Media Assistant", "Creative Studio KL", "Kuala Lumpur", "RM 2,000 - 2,800", "Manage content.", "Creative mindset.", "Marketing"),
        JobPost(6, "Healthcare Aide", "Sihat Care Centre", "Penang", "RM 1,900 - 2,400", "Meaningful career.", "SPM minimum.", "Healthcare")
    )

    // ── Search ────────────────────────────────────────────────────────────────
    var searchQuery by mutableStateOf("")
    var displayedQuery by mutableStateOf("")
    fun onSearchQueryChange(query: String) { searchQuery = query }
    fun onSearchSubmit() { displayedQuery = searchQuery }

    val filteredJobs: List<JobPost> get() = if (displayedQuery.isEmpty()) jobs else jobs.filter {
        it.title.contains(displayedQuery, ignoreCase = true) || it.company.contains(displayedQuery, ignoreCase = true) || it.category.contains(displayedQuery, ignoreCase = true)
    }

    // ── Chat ──────────────────────────────────────────────────────────────────
    private val _chatMessages = mutableStateOf<Map<Int, List<ChatMessage>>>(emptyMap())
    val chatMessages: Map<Int, List<ChatMessage>> get() = _chatMessages.value

    fun sendMessage(jobId: Int, message: String, senderName: String) {
        val existing = _chatMessages.value[jobId]?.toMutableList() ?: mutableListOf()
        val now = getCurrentTime()
        existing.add(ChatMessage("user", message, now))
        existing.add(ChatMessage("employer", "Thank you! We will review your message.", now))
        _chatMessages.value = _chatMessages.value.toMutableMap().also { it[jobId] = existing }
    }

    private fun getCurrentTime(): String {
        val cal = java.util.Calendar.getInstance()
        return String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
    }

    companion object {
        fun factory(repository: ApplicationRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = JobViewModel(repository) as T
            }
    }
}
