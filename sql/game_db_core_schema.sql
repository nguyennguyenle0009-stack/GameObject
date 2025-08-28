/* ============================
   GAME DB – CORE SCHEMA (MVP)
   ============================ */

-- 0) (Tuỳ chọn) Tạo database
IF DB_ID(N'GameDB') IS NULL
BEGIN
  CREATE DATABASE GameDB;
END
GO
USE GameDB;
GO

/* ----------------------------
   1) Players & Stats (Base/Runtime)
   ---------------------------- */
IF OBJECT_ID('dbo.PlayerRuntime','U') IS NOT NULL DROP TABLE dbo.PlayerRuntime;
IF OBJECT_ID('dbo.PlayerBaseStats','U') IS NOT NULL DROP TABLE dbo.PlayerBaseStats;
IF OBJECT_ID('dbo.Players','U') IS NOT NULL DROP TABLE dbo.Players;
GO

CREATE TABLE dbo.Players (
  PlayerId UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_Players PRIMARY KEY DEFAULT NEWID(),
  Name NVARCHAR(64) NOT NULL UNIQUE,
  Realm NVARCHAR(64) NULL,
  RealmStage INT NOT NULL DEFAULT 0,
  Physique NVARCHAR(64) NOT NULL DEFAULT 'NORMAL',
  CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE dbo.PlayerBaseStats (
  PlayerId UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_PlayerBaseStats PRIMARY KEY,
  Atk INT NOT NULL,
  Def INT NOT NULL,
  HealthMax INT NOT NULL,
  PepMax INT NOT NULL,
  Sould INT NOT NULL,
  Spirit INT NOT NULL,        -- EXP hiện tại
  SpiritMax INT NOT NULL,
  Strength INT NOT NULL,
  CONSTRAINT FK_BaseStats_Player FOREIGN KEY (PlayerId)
    REFERENCES dbo.Players(PlayerId) ON DELETE CASCADE
);

CREATE TABLE dbo.PlayerRuntime (
  PlayerId UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_PlayerRuntime PRIMARY KEY,
  CurrentHP INT NOT NULL,
  CurrentPep INT NOT NULL,
  MapId NVARCHAR(64) NOT NULL,
  PosX INT NOT NULL,
  PosY INT NOT NULL,
  Money BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT FK_Runtime_Player FOREIGN KEY (PlayerId)
    REFERENCES dbo.Players(PlayerId) ON DELETE CASCADE
);
GO

ALTER TABLE dbo.PlayerRuntime 
  ADD MapId NVARCHAR(64) NOT NULL DEFAULT N'unknown',
      PosX  INT NOT NULL DEFAULT 0,
      PosY  INT NOT NULL DEFAULT 0;

/* ----------------------------
   2) Items & Stat Mods
   ---------------------------- */
IF OBJECT_ID('dbo.ItemStatMods','U') IS NOT NULL DROP TABLE dbo.ItemStatMods;
IF OBJECT_ID('dbo.Items','U') IS NOT NULL DROP TABLE dbo.Items;
GO

CREATE TABLE dbo.Items (
  ItemId NVARCHAR(64) NOT NULL CONSTRAINT PK_Items PRIMARY KEY,
  Name NVARCHAR(128) NOT NULL,
  Type NVARCHAR(32) NOT NULL   -- ví dụ: 'weapon','armor','helmet','pants','shoes','amulet','ring','consumable','book'
);

CREATE TABLE dbo.ItemStatMods (
  Id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_ItemStatMods PRIMARY KEY,
  ItemId NVARCHAR(64) NOT NULL,
  Stat NVARCHAR(32) NOT NULL,      -- 'ATTACK','DEF','HEALTH_MAX','PEP_MAX',...
  Flat INT NOT NULL DEFAULT 0,     -- +x
  PercentBonus DECIMAL(6,3) NOT NULL DEFAULT 0.000, -- 0.10 = +10%
  CONSTRAINT FK_ItemStatMods_Items FOREIGN KEY (ItemId)
    REFERENCES dbo.Items(ItemId) ON DELETE CASCADE
);
GO

/* ----------------------------
   3) Inventory & Equipment
   ---------------------------- */
IF OBJECT_ID('dbo.PlayerEquipment','U') IS NOT NULL DROP TABLE dbo.PlayerEquipment;
IF OBJECT_ID('dbo.PlayerInventory','U') IS NOT NULL DROP TABLE dbo.PlayerInventory;
GO

CREATE TABLE dbo.PlayerInventory (
  PlayerId UNIQUEIDENTIFIER NOT NULL,
  ItemId NVARCHAR(64) NOT NULL,
  Quantity INT NOT NULL CONSTRAINT CK_PlayerInventory_Quantity CHECK (Quantity >= 0),
  CONSTRAINT PK_PlayerInventory PRIMARY KEY (PlayerId, ItemId),
  CONSTRAINT FK_PlayerInventory_Player FOREIGN KEY (PlayerId)
    REFERENCES dbo.Players(PlayerId) ON DELETE CASCADE,
  CONSTRAINT FK_PlayerInventory_Item FOREIGN KEY (ItemId)
    REFERENCES dbo.Items(ItemId)
);

-- Slot hợp lệ gợi ý: ARMOR, HELMET, PANTS, SHOES, WEAPON1, WEAPON2, NECKLACE, RING1, RING2, AMULET
CREATE TABLE dbo.PlayerEquipment (
  PlayerId UNIQUEIDENTIFIER NOT NULL,
  Slot NVARCHAR(16) NOT NULL,
  ItemId NVARCHAR(64) NULL,          -- NULL = none
  CONSTRAINT PK_PlayerEquipment PRIMARY KEY (PlayerId, Slot),
  CONSTRAINT FK_PlayerEquipment_Player FOREIGN KEY (PlayerId)
    REFERENCES dbo.Players(PlayerId) ON DELETE CASCADE,
  CONSTRAINT FK_PlayerEquipment_Item FOREIGN KEY (ItemId)
    REFERENCES dbo.Items(ItemId)
);
GO

/* ----------------------------
   4) Index gợi ý (tăng tốc load)
   ---------------------------- */
CREATE INDEX IX_PlayerInventory_Player ON dbo.PlayerInventory(PlayerId);
CREATE INDEX IX_PlayerEquipment_Player ON dbo.PlayerEquipment(PlayerId);
CREATE INDEX IX_ItemStatMods_Item ON dbo.ItemStatMods(ItemId);
GO

/* ----------------------------
   5) Seed dữ liệu mẫu
   ---------------------------- */
-- 5.1 Items
MERGE dbo.Items AS t
USING (VALUES
  (N'WEAPON#60fd3b79-0239-4c41-b3a1-07439360cdec', N'Kiếm gỗ',  N'weapon'),
  (N'ARMOR#13520a64-d4f0-4c64-82db-3bbe9e229386', N'Áo giáp',   N'armor')
) AS s(ItemId, Name, Type)
ON (t.ItemId = s.ItemId)
WHEN NOT MATCHED THEN
  INSERT (ItemId, Name, Type) VALUES (s.ItemId, s.Name, s.Type);

-- 5.2 ItemStatMods
-- Kiếm gỗ: +10 ATTACK
IF NOT EXISTS (SELECT 1 FROM dbo.ItemStatMods WHERE ItemId = N'WEAPON#60fd3b79-0239-4c41-b3a1-07439360cdec')
BEGIN
  INSERT INTO dbo.ItemStatMods (ItemId, Stat, Flat, PercentBonus)
  VALUES (N'WEAPON#60fd3b79-0239-4c41-b3a1-07439360cdec', N'ATTACK', 10, 0.000);
END

-- Áo giáp: +3 DEF
IF NOT EXISTS (SELECT 1 FROM dbo.ItemStatMods WHERE ItemId = N'ARMOR#13520a64-d4f0-4c64-82db-3bbe9e229386')
BEGIN
  INSERT INTO dbo.ItemStatMods (ItemId, Stat, Flat, PercentBonus)
  VALUES (N'ARMOR#13520a64-d4f0-4c64-82db-3bbe9e229386', N'DEF', 3, 0.000);
END
GO

/* ----------------------------
   6) Tạo 1 player mẫu để test
   ---------------------------- */
DECLARE @pid UNIQUEIDENTIFIER = NEWID();
INSERT INTO dbo.Players (PlayerId, Name, Realm, RealmStage, Physique)
VALUES (@pid, N'Nguyeen_pro', N'phàm nhân', 0, N'NORMAL');

INSERT INTO dbo.PlayerBaseStats (PlayerId, Atk, Def, HealthMax, PepMax, Sould, Spirit, SpiritMax, Strength)
VALUES (@pid, 5, 4, 100, 100, 5, 0, 1000, 1);

INSERT INTO dbo.PlayerRuntime (PlayerId, CurrentHP, CurrentPep, MapId, PosX, PosY, Money)
VALUES (@pid, 100, 100, N'world01', 100, 100, 0);

-- Inventory: có 1 kiếm gỗ, 1 áo giáp
INSERT INTO dbo.PlayerInventory (PlayerId, ItemId, Quantity)
VALUES 
  (@pid, N'WEAPON#60fd3b79-0239-4c41-b3a1-07439360cdec', 1),
  (@pid, N'ARMOR#13520a64-d4f0-4c64-82db-3bbe9e229386', 1);

-- Equip: mặc áo giáp, cầm kiếm gỗ ở WEAPON1
INSERT INTO dbo.PlayerEquipment (PlayerId, Slot, ItemId)
VALUES 
  (@pid, N'ARMOR',  N'ARMOR#13520a64-d4f0-4c64-82db-3bbe9e229386'),
  (@pid, N'WEAPON1',N'WEAPON#60fd3b79-0239-4c41-b3a1-07439360cdec');
GO

CREATE TABLE dbo.PlayerTechniques (
  PlayerId UNIQUEIDENTIFIER NOT NULL,
  Name NVARCHAR(128) NOT NULL,
  Grade NVARCHAR(16) NOT NULL,
  Level INT NOT NULL,
  SpiritPerSecond INT NOT NULL,
  CONSTRAINT PK_PlayerTechniques PRIMARY KEY (PlayerId, Name),
  CONSTRAINT FK_PlayerTechniques_Player FOREIGN KEY (PlayerId)
    REFERENCES dbo.Players(PlayerId) ON DELETE CASCADE
);
GO

-- Techniques: sample skill
INSERT INTO dbo.PlayerTechniques (PlayerId, Name, Grade, Level, SpiritPerSecond)
VALUES (N'Nguyeen_pro', N'Công pháp hạ phẩm', N'HA', 1, 1);

-- Create table to store serialized player profiles if it does not exist
IF NOT EXISTS (
    SELECT * FROM sys.objects
    WHERE object_id = OBJECT_ID(N'dbo.PlayerProfile') AND type = N'U'
)
BEGIN
    CREATE TABLE dbo.PlayerProfile (
        name NVARCHAR(100) NOT NULL PRIMARY KEY,
        profile NVARCHAR(MAX) NOT NULL
    );
END;
GO

/* ----------------------------
   7) Truy vấn test nhanh (đối chiếu)
   ---------------------------- */

-- Base + Runtime
SELECT p.Name, bs.Atk, bs.Def, bs.HealthMax, bs.PepMax, rt.CurrentHP, rt.CurrentPep, rt.Money
FROM dbo.Players p
JOIN dbo.PlayerBaseStats bs ON p.PlayerId = bs.PlayerId
JOIN dbo.PlayerRuntime rt ON p.PlayerId = rt.PlayerId
WHERE p.Name = N'Nguyeen_pro';

-- Equipment hiện tại
SELECT e.Slot, e.ItemId, i.Name, i.Type
FROM dbo.PlayerEquipment e
LEFT JOIN dbo.Items i ON e.ItemId = i.ItemId
WHERE e.PlayerId = (SELECT PlayerId FROM dbo.Players WHERE Name = N'Nguyeen_pro')
ORDER BY e.Slot;

-- Tính modifier từ trang bị (server sẽ dùng để tính effective)
SELECT e.Slot, ism.Stat, SUM(ism.Flat) AS SumFlat, SUM(ism.PercentBonus) AS SumPercent
FROM dbo.PlayerEquipment e
JOIN dbo.ItemStatMods ism ON e.ItemId = ism.ItemId
WHERE e.PlayerId = (SELECT PlayerId FROM dbo.Players WHERE Name = N'Nguyeen_pro')
GROUP BY e.Slot, ism.Stat
ORDER BY e.Slot, ism.Stat;

-- Learned techniques
SELECT Name, Grade, Level, SpiritPerSecond
FROM dbo.PlayerTechniques
WHERE PlayerId = (SELECT PlayerId FROM dbo.Players WHERE Name = N'Nguyeen_pro');