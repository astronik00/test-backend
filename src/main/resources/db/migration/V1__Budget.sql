--- Бюджет
create table if not exists budget
(
    id     serial primary key,
    year   int  not null,
    month  int  not null,
    amount int  not null,
    type   text not null
);

COMMENT ON TABLE budget         IS 'Бюджет.';
COMMENT ON COLUMN budget.id     IS 'Идентификатор';
COMMENT ON COLUMN budget.year   IS 'Отчетный год';
COMMENT ON COLUMN budget.month  IS 'Отчетный месяц';
COMMENT ON COLUMN budget.amount IS 'Величина';
COMMENT ON COLUMN budget.type   IS 'Тип операции';