package jp.vpopov.ghwarriors.core.decompose

import androidx.annotation.CallSuper
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Abstract base class for ViewModels used with Decompose architecture.
 *
 * This class provides lifecycle-aware coroutine scope management that integrates with
 * Decompose's InstanceKeeper system. The viewModelScope is automatically cancelled
 * when the component is destroyed.
 *
 * @param dispatchers Coroutine dispatchers configuration for the viewModel scope
 */
abstract class DecomposeViewModel(
    dispatchers: AppDispatchers
) : InstanceKeeper.Instance {
    /**
     * Coroutine scope tied to the lifecycle of this ViewModel.
     * Uses SupervisorJob to prevent child coroutine failures from cancelling siblings,
     * and runs on the main dispatcher by default.
     */
    protected val viewModelScope = CoroutineScope(SupervisorJob() + dispatchers.main)

    /**
     * Called when the InstanceKeeper.Instance is being destroyed.
     * Cancels the viewModelScope to clean up any running coroutines.
     *
     * Subclasses should call super.onDestroy() when overriding this method.
     */
    @CallSuper
    override fun onDestroy() {
        viewModelScope.cancel()
    }
}