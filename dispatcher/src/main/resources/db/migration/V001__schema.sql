create table users
(
    id            int4 generated by default as identity,
    chat_id       int8,
    name          varchar(255),
    storage_id    int4,
    user_state_id int4,
    primary key (id)
);

create table user_state
(
    id             int4 generated by default as identity,
    card_id        int4,
    card_stock_id  int4,
    current_menu   varchar(255),
    studying_state TEXT,
    primary key (id)
);

alter table if exists users
    drop constraint if exists UK_8xpfvbvc9tew2viqgf2mivqr6;
alter table if exists users
    add constraint UK_8xpfvbvc9tew2viqgf2mivqr6 unique (user_state_id);
alter table if exists users
    add constraint FKmi5y7n2upe24nkpgnnns2d1a foreign key (user_state_id) references user_state;
