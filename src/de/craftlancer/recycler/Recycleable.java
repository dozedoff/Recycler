package de.craftlancer.recycler;

public class Recycleable
{
    protected int id;
    protected int rewardid;
    protected int rewardamount;
    protected double maxdura;
    protected double extradura;
    protected boolean calcdura;
    
    public Recycleable(int id, int rewardid, int rewardamount, int maxdura, int extradura, boolean calcdura)
    {
        this.id = id;
        this.rewardid = rewardid;
        this.rewardamount = rewardamount;
        this.maxdura = maxdura;
        this.extradura = extradura;
        this.calcdura = calcdura;
    }
}
