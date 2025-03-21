package main;

public interface IImageSelector {
public int getNextImageID();
public void tick();
public void setParams(int kind, int min, int max, int limit);
}
