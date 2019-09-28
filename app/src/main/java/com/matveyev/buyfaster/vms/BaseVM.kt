package com.matveyev.buyfaster.vms

import kotlin.random.Random

open class BaseVM {
    val id = Random.nextLong()
}