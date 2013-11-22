/* Datastructures for graph representation adapted from work by Guillaume Artignan
 * Copyright (C)2010 Guillaume Artignan
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
package donatien.model.graph;



import java.util.HashMap;


/**
 * All propertyModifiable instances have dynamic attributes.
 * A dynamic property is a couple (Name of the property, Value of the property), noted currently (key,val)
  *
 */
public class PropertyModifiable 
{
	/**Dynamic properties*/
	protected HashMap<String,String> prop = new HashMap<String,String>();
		
	/**System Markers*/
	protected HashMap<Object,Object> system_prop = new HashMap<Object,Object>();
	
	/**Add a property with it value*/
	public void putProp(String key,String val){ prop.put(key,val);}
		
	/**Add a System prop*/
	public void putSystemProp(Object key,Object val){	system_prop.put(key,val);}
	
	/**
	 * The method return the value of the property key given in parameter.
	 * If the property doesn't exist return null.
	 * 
	 * @param key
	 * @return the value associated to the property key.
	 */
	public String getProp(String key)	{return prop.get(key);	}
	
	
	/**
	 * The method return the value of the property key given in parameter.
	 * If the property doesn't exist return null.
	 * 
	 * @param key
	 * @return the value associated to the property key.
	 */
	public Object getSystemProp(Object key)	{return system_prop.get(key);	}
	
	/**
	 * Return all properties names.
	 * 
	 * @return An array of property names
	 */
	public String[] getAllKeys()
	{
		return prop.keySet().toArray(new String[0]);
	}
	
	/**
	 * Return all properties names.
	 * 
	 * @return An array of property names
	 */
	public String[] getAllSystemsKeys()
	{
		return system_prop.keySet().toArray(new String[0]);
	}
	
	/**
	* Remove a property names key in the dynamic properties.
	* 
	**/
	public void removeProp(String key)
	{
		prop.remove(key);
	}
	
	/**
	* Remove system properties
	* 
	**/
	public void removeSystemProp(Object marker)
	{
		system_prop.remove(marker);
	}
}
