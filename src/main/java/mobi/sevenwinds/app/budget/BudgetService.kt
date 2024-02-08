package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    private class ILikeOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "ILIKE")

    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.author =
                    if (body.authorId == null) null else AuthorTable.select { AuthorTable.id eq body.authorId }
                        .limit(1)
                        .singleOrNull()?.let { AuthorEntity.wrapRow(it) }
            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val fio = param.fio
            val query = if (fio != null)
                (BudgetTable innerJoin AuthorTable)
                    .select { BudgetTable.year eq param.year }
                    .andWhere { AuthorTable.fio ilike "%$fio%" }
            else
                BudgetTable
                    .select { BudgetTable.year eq param.year }

            query
                .orderBy(BudgetTable.month)
                .orderBy(BudgetTable.amount, SortOrder.DESC)

            val total = query.count()
            val totalData = BudgetEntity.wrapRows(query).map { it.toBudgetAuthorResponse() }

            query.limit(param.limit, param.offset)

            val paginatedData = BudgetEntity.wrapRows(query).map { it.toBudgetAuthorResponse() }
            val sumByType = totalData.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = paginatedData
            )
        }
    }

    private infix fun <T : String?> ExpressionWithColumnType<T>.ilike(pattern: String): Op<Boolean> =
        ILikeOp(this, QueryParameter(pattern, columnType))
}