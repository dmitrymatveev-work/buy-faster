package com.matveyev.buyfaster

import android.app.Application
import com.matveyev.buyfaster.data.DepRepository
import com.matveyev.buyfaster.data.TheListRepository

class BuyFasterApp : Application() {
    val theListRepository = lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        TheListRepository(applicationContext)
    }

    val depsRepository = lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        DepRepository(applicationContext)
    }
}