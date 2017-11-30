package hardware;

import memory.Cache;
import memory.Port;
import memory.Register;
import process.Process;
import process.Semaphore;
import process.State;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MultiCoreCPU {
	
	private Register register = new Register();
	private Cache cache = new Cache();
	Clock clock;
	ArrayList<Semaphore> semlist;
	
	
	Core core1;
	Core core2;
	Core core3;
	Core core4;
	

	public MultiCoreCPU(Clock clock, ArrayList<Semaphore> semList) {
		this.clock = clock;
		this.semlist = semList;
		core1 = new Core(semlist);
		core2 = new Core(semlist);
		core3 = new Core(semlist);
		core4 = new Core(semlist);
	}


	public ArrayList<Semaphore> getSemlist() {
		return semlist;
	}


	public void setSemlist(ArrayList<Semaphore> semlist) {
		this.semlist = semlist;
	}


	public Core getCore1() {
		return core1;
	}


	public void setCore1(Core core1) {
		this.core1 = core1;
	}


	public Core getCore2() {
		return core2;
	}


	public void setCore2(Core core2) {
		this.core2 = core2;
	}


	public Core getCore3() {
		return core3;
	}


	public void setCore3(Core core3) {
		this.core3 = core3;
	}


	public Core getCore4() {
		return core4;
	}


	public void setCore4(Core core4) {
		this.core4 = core4;
	}


	public Register getRegister() {
		return register;
	}


	public void setRegister(Register register) {
		this.register = register;
	}


	public Cache getCache() {
		return cache;
	}


	public void setCache(Cache cache) {
		this.cache = cache;
	}


	public Clock getClock() {
		return clock;
	}


	public void setClock(Clock clock) {
		this.clock = clock;
	}

	
	

	
	
	
}
