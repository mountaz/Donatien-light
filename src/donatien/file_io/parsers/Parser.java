/* GraphML parser used to parse GraphML graphs
 * Copyright (C)2009 Guillaume Artignan
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
package donatien.file_io.parsers;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import donatien.file_io.exception.NotSupportedFormatException;
import donatien.model.graph.Graph;




public abstract class Parser
{
	/*Abstracts Methods*/
	
	public abstract donatien.model.graph.Graph parse(Reader r) throws IOException, NotSupportedFormatException;

	/*Implemented Methods*/
	
	public Graph parse(File f) throws IOException, NotSupportedFormatException
	{
		return parse(new FileReader(f));
	}
	
	public Graph parse(URL u) throws IOException, NotSupportedFormatException
	{
		return parse(u.openStream());
	}
		
	public Graph parse(InputStream r) throws IOException, NotSupportedFormatException
	{
		return parse(new InputStreamReader(r));
	}
}
