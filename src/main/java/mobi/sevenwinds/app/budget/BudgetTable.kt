package mobi.sevenwinds.app.budget

import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object BudgetTable : IntIdTable("budget") {
    val year = integer("year")
    val month = integer("month")
    val amount = integer("amount")
    val type = enumerationByName("type", 100, BudgetType::class)
    var authorId = optReference("author_id", AuthorTable)
}

class BudgetEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BudgetEntity>(BudgetTable)

    var year by BudgetTable.year
    var month by BudgetTable.month
    var amount by BudgetTable.amount
    var type by BudgetTable.type
    var author by AuthorEntity optionalReferencedOn BudgetTable.authorId

    fun toResponse(): BudgetRecord {
        return BudgetRecord(year, month, amount, type, author?.id?.value)
    }

    fun toBudgetAuthorResponse(): BudgetStatsResponse {
        return BudgetStatsResponse(
            year = year,
            month = month,
            amount = amount,
            type = type,
            fio = author?.fio,
            createDate = author?.createDate?.toString("dd-MM-yyyy HH:mm:ss")
        )
    }

}