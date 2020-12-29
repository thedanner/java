package game.level;

public class Rid
{
    public final static int BG_ROWS = (AbstractLevel.WORLD_HEIGHT / AbstractLevel.BG_TILE_HEIGHT);
    public final static int BG_COLS = (AbstractLevel.WORLD_WIDTH / AbstractLevel.BG_TILE_WIDTH);
    /* Tile reference id (RID) magic numbers */
    public final static int RID_NO_TILE = 0;
    public final static int RID_BG = 1; // (background layer)
    public final static int RID_WALL = 30; // (collision layer)
    public final static int RID_PC = 27;
    public final static int RID_ENEMY = 39;
    public final static int RID_GOAL = 29;
    public final static int RID_ENEMY_BLOCK = 71;
    public final static int RID_BACON = 10;
    public final static int RID_ROCKET = 34;
    public final static int RID_GROUND = 30;
    public final static int RID_BLOCK_PLATFORM = 30;
    public final static int RID_SPIKE = 18;
    public final static int RID_KFC = 40;
    public final static int[] RID_TRAPS = { 18 };
    public final static int[] RID_PLATFORMS = { 37, 28, RID_KFC };
    public final static int[] RID_LADDAS = { 26, 36, 46, 56, 66 };
}
