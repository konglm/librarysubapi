CREATE TABLE #(tableName) (
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [pid] [bigint] NOT NULL DEFAULT(0),
  [name] [varchar](20) NOT NULL DEFAULT(''),
  [sort] [bigint] NOT NULL DEFAULT(0),
  [status] [smallint] NOT NULL DEFAULT(0),
  [del] [bit] NOT NULL DEFAULT(0),
  CONSTRAINT [IDX_#(tableName)] PRIMARY KEY(id)
);