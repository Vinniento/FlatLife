package fh.wfp2.flatlife.data.room.entities.mapping

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import fh.wfp2.flatlife.data.room.entities.FinanceActivity

@Entity
data class ExpenseCategoryWithFinanceActivity(
        @Embedded val category: ExpenseCategory,
        @Relation(
                parentColumn = "categoryId",
                entityColumn = "activityId"
        )
        val activities: List<FinanceActivity>
)