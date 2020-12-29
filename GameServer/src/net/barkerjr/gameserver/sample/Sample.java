/*
 * Game Server Library - Querying server requests and returning them as XML
 * Copyright (C) 2007-2009  BarkerJr <http://www.barkerjr.net/java/GameServer/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.barkerjr.gameserver.sample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeoutException;

import net.barkerjr.gameserver.GameServer;
import net.barkerjr.gameserver.Listener;
import net.barkerjr.gameserver.GameServer.Request;
import net.barkerjr.gameserver.gamespy.Battlefield1942Server;
import net.barkerjr.gameserver.plugins.GameMonitor;
import net.barkerjr.gameserver.unreal.UnrealEngine1Server;
import net.barkerjr.gameserver.unreal.UnrealEngine2Server;
import net.barkerjr.gameserver.valve.GoldSourceServer;
import net.barkerjr.gameserver.valve.Left4DeadServer;
import net.barkerjr.gameserver.valve.SourceServer;
import net.barkerjr.gameserver.valve.SourceServerList;
import net.barkerjr.gameserver.valve.ValveServerList;

/**
 * This sample queries servers and includes benchmarking of each query.
 * 
 * @author BarkerJr
 */
public abstract class Sample {
	/** The amount of time to wait for each server response */
	private static final int TIMEOUT = 2000;
	
	/**
	 * A simple error handler
	 * 
	 * @author BarkerJr
	 */
	private static class SampleListener extends Listener {
		/**
		 * Prints the error to the console
		 */
		@Override
		public void errorHandler(Throwable error, GameServer server) {
			System.err.println("Error querying: " + server);
			error.printStackTrace();
		}
	}
	
	/**
	 * Queries the servers
	 * 
	 * @param args  this is currently not used
	 * @throws IOException  if it's thrown by a server
	 * @throws TimeoutException  if a server is down
	 * @throws InterruptedException  if the thread is interrupted
	 */
	public static void main(String[] args)
			throws IOException, TimeoutException, InterruptedException {
		long duration = 0;
		
		try {
		long serverStarted = System.currentTimeMillis();
//		processSourceServer("99.198.108.173", 27015, false);
		long serverDuration = System.currentTimeMillis() - serverStarted;
//		duration += serverDuration;
//		System.out.println("Server(" + serverDuration + "ms) Total(" + duration + "ms)");
//		System.out.println("NOTE: The first query is expected to be long, " +
//				"as the classloader caches everything.");
//		
//		System.out.println();
		
		serverStarted = System.currentTimeMillis();
		processLeft4DeadServer("67.212.78.185", 27019, false);
		serverDuration = System.currentTimeMillis() - serverStarted;
		duration += serverDuration;
		System.out.println("Server(" + serverDuration + "ms) Total(" + duration + "ms)");
		
		System.out.println();
		
//		serverStarted = System.currentTimeMillis();
//		processGoldSourceServer("72.9.146.117", 27015);
//		serverDuration = System.currentTimeMillis() - serverStarted;
//		duration += serverDuration;
//		System.out.println("Server(" + serverDuration + "ms) Total(" + duration + "ms)");
//
//		System.out.println();
//		
//		serverStarted = System.currentTimeMillis();
//		processUnrealEngine1Server("69.93.101.77", 7778);
//		serverDuration = System.currentTimeMillis() - serverStarted;
//		duration += serverDuration;
//		System.out.println("Server(" + serverDuration + "ms) Total(" + duration + "ms)");
//
//		System.out.println();
//		
//		serverStarted = System.currentTimeMillis();
//		processBattlefield1942Server("200.177.229.251", 23000);
//		serverDuration = System.currentTimeMillis() - serverStarted;
//		duration += serverDuration;
//		System.out.println("Server(" + serverDuration + "ms) Total(" + duration + "ms)");
//		
//		System.out.println();
		
//		serverStarted = System.currentTimeMillis();
//		processUnrealEngine2Server("68.232.166.168", 7708);
//		serverDuration = System.currentTimeMillis() - serverStarted;
//		duration += serverDuration;
//		System.out.println("Server(" + serverDuration + "ms) Total(" + duration + "ms)");
//		
//		System.out.println();
		
//		System.out.println("Fetching the Synergy server list...");
//		serverStarted = System.currentTimeMillis();
//		SourceServerList list = new SourceServerList();
//		list.gameDir = "Synergy";
//		ValveServerList<SourceServer>.ServerIterator servers = list.iterator(1000);
//		try {
//			while (servers.hasNext()) {
//				SourceServer server = servers.next();
//				System.out.println(server);
//				server.close();
//			}
//			System.out.println("From: " + servers.getMaster());
//		} finally {
//			servers.close();
//		}
//		serverDuration = System.currentTimeMillis() - serverStarted;
//		duration += serverDuration;
//		System.out.println("ServerList(" + serverDuration + "ms) Total(" + duration + "ms)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetches the server data then prints it to the console
	 * 
	 * @param ip  the IP to query
	 * @param port  the port to query
	 * @throws IOException  if it's thrown by the server
	 * @throws TimeoutException  if the server is down
	 * @throws InterruptedException  if the thread is interrupted
	 */
	private static void processSourceServer(String ip, int port,
			boolean useGameMonitor)
			throws IOException, TimeoutException, InterruptedException {
		System.out.println("Processing a Source server...");
		
		InetSocketAddress addr = new InetSocketAddress(ip, port);
		SourceServer server = new SourceServer(addr);
		server.addListener(new SampleListener());
		if (useGameMonitor) {
			server.addPlugin(new GameMonitor());
		}
		try {
			server.load(TIMEOUT, Request.INFORMATION, Request.PLAYERS);
		} finally {
			server.close();
		}
		
		System.out.print(server);
	}

	/**
	 * Fetches the server data then prints it to the console
	 * 
	 * @param ip  the IP to query
	 * @param port  the port to query
	 * @throws IOException  if it's thrown by the server
	 * @throws TimeoutException  if the server is down
	 * @throws InterruptedException  if the thread is interrupted
	 */
	private static void processLeft4DeadServer(String ip, int port,
			boolean useGameMonitor)
			throws IOException, TimeoutException, InterruptedException {
		System.out.println("Processing a Left4Dead server...");
		
		InetSocketAddress addr = new InetSocketAddress(ip, port);
		Left4DeadServer server = new Left4DeadServer(addr);
		server.addListener(new SampleListener());
		if (useGameMonitor) {
			server.addPlugin(new GameMonitor());
		}
		try {
			server.load(TIMEOUT, Request.INFORMATION, Request.PLAYERS, Request.RULES);
		} finally {
			server.close();
		}
		
		System.out.print(server);
	}

	/**
	 * Fetches the server data then prints it to the console
	 * 
	 * @param ip  the IP to query
	 * @param port  the port to query
	 * @throws IOException  if it's thrown by the server
	 * @throws TimeoutException  if the server is down
	 * @throws InterruptedException  if the thread is interrupted
	 */
	private static void processGoldSourceServer(String ip, int port)
			throws IOException, TimeoutException, InterruptedException {
		System.out.println("Processing a Gold Source server...");
		
		GoldSourceServer server =
			new GoldSourceServer(new InetSocketAddress(ip, port));
		server.addListener(new SampleListener());
		try {
			server.load(TIMEOUT, Request.INFORMATION, Request.PLAYERS,
					Request.RULES);
		} finally {
			server.close();
		}
		
		System.out.print(server);
	}

	/**
	 * Fetches the server data then prints it to the console
	 * 
	 * @param ip  the IP to query
	 * @param port  the port to query
	 * @throws IOException  if it's thrown by the server
	 * @throws InterruptedException  if the thread is interrupted
	 * @throws TimeoutException  if the server is down
	 */
	private static void processUnrealEngine1Server(String ip, int port)
			throws IOException, InterruptedException, TimeoutException {
		System.out.println("Processing an Unreal Tournament server...");
		
		UnrealEngine1Server server =
			new UnrealEngine1Server(new InetSocketAddress(ip, port));
		server.addListener(new SampleListener());
		try {
			server.load(TIMEOUT, Request.BASIC, Request.INFORMATION,
					Request.PLAYERS, Request.RULES);
		} finally {
			server.close();
		}
		
		System.out.print(server);
	}

	/**
	 * Fetches the server data then prints it to the console
	 * 
	 * @param ip  the IP to query
	 * @param port  the port to query
	 * @throws IOException  if it's thrown by the server
	 * @throws InterruptedException  if the thread is interrupted
	 * @throws TimeoutException  if the server is down
	 */
	private static void processBattlefield1942Server(String ip, int port)
			throws IOException, InterruptedException, TimeoutException {
		System.out.println("Processing a Battlefield 1942 server...");
		
		Battlefield1942Server server =
			new Battlefield1942Server(new InetSocketAddress(ip, port));
		server.addListener(new SampleListener());
		try {
			server.load(TIMEOUT, Request.BASIC, Request.INFORMATION,
					Request.PLAYERS, Request.RULES);
		} finally {
			server.close();
		}
		
		System.out.print(server);
	}
	
	/**
	 * TODO
	 */
	private static void processUnrealEngine2Server(String ip, int port)
			throws IOException, InterruptedException, TimeoutException {
		System.out.println("Processing a Unreal Engine 2 server...");
		
		UnrealEngine2Server server =
			new UnrealEngine2Server(new InetSocketAddress(ip, port));
		server.addListener(new SampleListener());
		try {
			server.load(TIMEOUT, Request.INFORMATION, Request.PLAYERS, Request.RULES);
		} finally {
			server.close();
		}
		
		System.out.print(server);
	}
}