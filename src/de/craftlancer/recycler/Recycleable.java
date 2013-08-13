package de.craftlancer.recycler;

public class Recycleable
{
    private int id;
    private int rewardid;
    private int rewardamount;
    private double maxdura;
    private double extradura;
    private boolean calcdura;
    
    public Recycleable(int id, int rewardid, int rewardamount, int maxdura, int extradura, boolean calcdura)
    {
        this.id = id;
        this.rewardid = rewardid;
        this.rewardamount = rewardamount;
        this.maxdura = maxdura;
        this.extradura = extradura;
        this.calcdura = calcdura;
    }
    
    public int getId()
    {
        return id;
    }
    
    public int getRewardid()
    {
        return rewardid;
    }
    
    public int getRewardamount()
    {
        return rewardamount;
    }
    
    public double getMaxdura()
    {
        return maxdura;
    }
    
    public double getExtradura()
    {
        return extradura;
    }
    
    public boolean isCalcdura()
    {
        return calcdura;
    }
}
