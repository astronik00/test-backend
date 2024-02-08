--- Автор записи
create table if not exists author
(
    id          serial primary key,
    fio         text        not null,
    create_date timestamptz not null
);

COMMENT ON TABLE author              IS 'Автор записи.';
COMMENT ON COLUMN author.id          IS 'Идентификатор';
COMMENT ON COLUMN author.fio         IS 'Полное имя автора';
COMMENT ON COLUMN author.create_date IS 'Дата и время создания записи';

alter table budget add author_id int references author (id);

COMMENT ON COLUMN budget.author_id IS 'Идентификатор автора записи';