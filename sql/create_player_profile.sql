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
