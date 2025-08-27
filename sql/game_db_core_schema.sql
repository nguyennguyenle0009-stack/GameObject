CREATE TABLE Players (
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE PlayerBaseStats (
    player_id INT PRIMARY KEY FOREIGN KEY REFERENCES Players(id),
    health INT,
    pep INT,
    attack INT,
    def INT,
    strength INT,
    sould INT
);

CREATE TABLE PlayerRuntime (
    player_id INT PRIMARY KEY FOREIGN KEY REFERENCES Players(id),
    spirit INT
);

CREATE TABLE PlayerInventory (
    player_id INT FOREIGN KEY REFERENCES Players(id),
    item_name NVARCHAR(100),
    quantity INT,
    PRIMARY KEY(player_id, item_name)
);

CREATE TABLE PlayerEquipment (
    player_id INT FOREIGN KEY REFERENCES Players(id),
    slot NVARCHAR(20),
    item_name NVARCHAR(100),
    PRIMARY KEY(player_id, slot)
);

CREATE TABLE Items (
    name NVARCHAR(100) PRIMARY KEY,
    description NVARCHAR(255),
    type NVARCHAR(20),
    icon NVARCHAR(255)
);

CREATE TABLE ItemStatMods (
    item_name NVARCHAR(100) FOREIGN KEY REFERENCES Items(name),
    attr NVARCHAR(20),
    value INT,
    PRIMARY KEY(item_name, attr)
);
