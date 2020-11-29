/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author c3338047 Yuji
 */

public class Process {
    private String iD;
    private int arrive;
    private int execSize;
    private int priority;
    private int turnAround;
    private int wait;
    private int log;
    private int finish;

    public Process(String new_ID, int new_arrive, int new_excSize, int new_priority)
    {
        this.iD = new_ID;
        this.arrive = new_arrive;
        this.execSize = new_excSize;
        this.priority = new_priority;
        this.turnAround = -999;
        this.wait = -999;
        this.log = -999;
        this.finish = -999;
    }

    public void setTime(int new_turnAround, int new_wait)
    {
        turnAround = new_turnAround;
        wait = new_wait;
    }

    public void setExecSize(int new_excSize)
    {
        execSize = new_excSize;
    }

    public void setLog(int new_log)
    {
        log = new_log;
    }
    public void setFin(int new_finish)
    {
        finish = new_finish;
    }

    public String getID()
    {
        return this.iD;
    }

    public int getArrive()
    {
        return this.arrive;
    }

    public int getExcSize()
    {
        return this.execSize;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public int getFin(){return this.finish; }
    public int getTurnAround()
    {
        return this.turnAround;
    }

    public int getWait()
    {
        return this.wait;
    }

    public int gettLog()
    {
        return this.log;
    }
}

