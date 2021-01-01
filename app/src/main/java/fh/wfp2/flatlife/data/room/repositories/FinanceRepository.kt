package fh.wfp2.flatlife.data.room.repositories

import fh.wfp2.flatlife.data.room.daos.FinanceDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class FinanceRepository {
        val financeRepositoryJob = Job()

        private val ioScope = CoroutineScope(financeRepositoryJob + Dispatchers.IO)

    }

