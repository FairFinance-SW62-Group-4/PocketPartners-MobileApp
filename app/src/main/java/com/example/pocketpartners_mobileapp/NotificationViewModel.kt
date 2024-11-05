import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableLiveData<MutableList<String>>(mutableListOf())
    val notifications: LiveData<MutableList<String>> get() = _notifications

    fun addNotification(notification: String) {
        _notifications.value?.add(notification)
        _notifications.postValue(_notifications.value) // Usar postValue
    }

    fun removeNotification(notification: String) {
        _notifications.value?.remove(notification)
        _notifications.postValue(_notifications.value) // Usar postValue
    }
}

