DROP TABLE IF EXISTS shedlock;

CREATE TABLE shedlock(
         name VARCHAR(64) NOT NULL,
         lock_until TIMESTAMP(3) NOT NULL,
         locked_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
         locked_by VARCHAR(255) NOT NULL,
         PRIMARY KEY (name)
);

DROP TABLE IF EXISTS contract_offset;

CREATE TABLE contract_offset(
    contract_address VARCHAR(100) NOT NULL,
    contract_name varchar(50) not null,
    block_offset bigint not null default 0,
    recorded_at timestamp(3) NOT NULL default current_timestamp(3),
    primary key (contract_address)
) engine = innodb;

insert into contract_offset(contract_address, contract_name, block_offset) values ('0x829e62f57e243180605858548e0051D5582c3A51', 'XracerStakingPool', 0);
insert into contract_offset(contract_address, contract_name, block_offset) values ('0xdc479220ad98B5eaac308218Eb1d9a6d222F6fe0', 'XCarNFT', 0);
insert into contract_offset(contract_address, contract_name, block_offset) values ('0x6D18276e0DC29259Dc408B81a9F6eC2809156Cdb', 'ReferrerControl', 0);
insert into contract_offset(contract_address, contract_name, block_offset) values ('0x842c08dEA9bFaAF9B2643b64799e33d4CD1E09E6', 'Upgrader', 0);


DROP TABLE IF EXISTS user_nft_cards;

CREATE TABLE user_nft_cards (
    id bigint not null primary key auto_increment,
    owner_address varchar(100) not null,
    tx_hash varchar(255) not null,
    token_id int not null,
    pool_id int not null default 0,
    rarity int not null,
    level int not null,
    speed bigint not null,
    is_staked boolean not null default false,
    created_at timestamp(3) NOT NULL default current_timestamp(3),
    updated_at timestamp(3) NOT NULL default current_timestamp(3),
    unique INDEX user_address_idx(owner_address, token_Id)
) engine=InnoDB;

DROP TABLE IF EXISTS user_nft_cards_transfer;

CREATE TABLE user_nft_cards_transfer(
    id bigint not null primary key auto_increment,
    tx_hash varchar(255) not null,
    from_address varchar(100) not null,
    to_address varchar(100) not null,
    token_id int not null,
    created_at timestamp(3) NOT NULL default current_timestamp(3),
    updated_at timestamp(3) NOT NULL default current_timestamp(3),
    unique index user_nft_cards_transfer_idx(from_address, to_address, token_id)
) engine=InnoDB;


DROP TABLE IF EXISTS  user_nft_cards_staking;

create table user_nft_cards_staking( # 质押和解质押事实表
    id bigint not null primary key auto_increment,
    owner_address varchar(100) not null,
    tx_hash varchar(255) not null,
    token_id int not null,
    pool_id int not null default 1,
    event varchar(20) not null, # 动作，Staking or Withdraw
    created_at timestamp(3) NOT NULL default current_timestamp(3),
    updated_at timestamp(3) NOT NULL default current_timestamp(3),
    unique index user_staking_cards_idx(owner_address, token_id, event )
) engine=InnoDB;

DROP TABLE IF EXISTS t_referrer;
create table t_referrer (
    id bigint not null primary key auto_increment,
    tx_hash varchar(255) not null,
    referrer_address varchar(100) not null,
    referal_address varchar(100) not null,
    is_actived boolean not null default false,
    created_at timestamp(3) NOT NULL default current_timestamp(3),
    updated_at timestamp(3) NOT NULL default current_timestamp(3),
    unique index referrer_idx(referrer_address, referal_address)
) engine=InnoDB;
