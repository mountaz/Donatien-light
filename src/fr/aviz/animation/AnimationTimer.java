/* This software is a part of Aviz API 
 * Copyright (C)2008 Pierre Dragicevic 
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
 * along with this program.  If not, see<http://www.gnu.org/licenses/>.
 */
package fr.aviz.animation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

/**
 * This is the time scheduling part of the animation.
 * 
 * Note that AnimatedFloat is pretty much independent from this class and can rely on a main loop
 * instead (e.g., open GL).
 * 
 */
public class AnimationTimer {
	
	public static final int ANIMATION_RATE = 17; // ms
	
	static ArrayList<AnimationListener> listeners = new ArrayList<AnimationListener>();

	static Timer animationTimer = new Timer(ANIMATION_RATE, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			animateEvent();
		}
	});
	
	public static void addListener(AnimationListener listener) {
		listeners.add(listener);
	}
	
	public static boolean isRunning() {
		return animationTimer.isRunning();
	}
	
	static void maybeAnimate() {
		if (!animationTimer.isRunning() && AnimatedFloat.needsAnimation()) {
			animationTimer.start();
//			System.out.println("Animation started...");
			fireAnimationEvent();
		}
	}

	private static void animateEvent() {
		if (!AnimatedFloat.animateAll()) {
			animationTimer.stop();
//			System.out.println("   Animation stopped.");
		}
		fireAnimationEvent();
	}
	
	public static void fireAnimationEvent() {
		for (AnimationListener l : listeners)
			l.animateEvent();
	}
	

}
