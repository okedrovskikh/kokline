package kek.team.kokline.factories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

val coroutinePool: CoroutineDispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
