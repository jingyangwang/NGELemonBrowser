/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package io.github.mthli.Ninja.Unit;

import android.os.Environment;

/**
 * Defines constants.
 */
public class Constants {
	public static final String File_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lemon_browser/";
	public static final String IMAGECACHE_PATH = File_PATH + "imagecache/";
	public static final String IMAGECACHE_NOMEDIA = IMAGECACHE_PATH + ".nomedia";
	public static final String DOWNLOAD_PATH = File_PATH + "download/";

}
