package jp.vpopov.ghwarriors.core.decompose

import androidx.annotation.CallSuper
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class DecomposeViewModel : InstanceKeeper.Instance {
    protected val viewModelScope = CoroutineScope(SupervisorJob())

    @CallSuper
    override fun onDestroy() {
        viewModelScope.cancel()
    }
}