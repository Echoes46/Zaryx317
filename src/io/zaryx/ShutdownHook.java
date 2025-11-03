package io.zaryx;

import io.zaryx.content.wogw.Wogw;

public class ShutdownHook extends Thread {

	public ShutdownHook() {
		setName("shutdown-hook");
	}

	public void run() {
		System.out.println("Successfully executed ShutdownHook");
		Wogw.save();
	}
}
