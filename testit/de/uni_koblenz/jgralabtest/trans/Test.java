/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2010 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 * 
 *               ist@uni-koblenz.de
 * 
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.uni_koblenz.jgralabtest.trans;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.uni_koblenz.jgralabtest.schemas.motorwaymap.City;

import de.uni_koblenz.jgralab.JGraLabSet;
import de.uni_koblenz.jgralab.trans.InvalidSavepointException;
import de.uni_koblenz.jgralab.trans.Savepoint;

import de.uni_koblenz.jgralabtest.schemas.motorwaymap.MotorwayMap;
import de.uni_koblenz.jgralabtest.schemas.motorwaymap.MotorwayMapSchema;
import de.uni_koblenz.jgralabtest.schemas.motorwaymap.TestRecord;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		MotorwayMap motorwayMap = MotorwayMapSchema.instance()
				.createMotorwayMapWithTransactionSupport();
		motorwayMap.newTransaction();
		City city1 = motorwayMap.createCity();
		List<TestRecord> _testList = motorwayMap.createList();
		List<String> list1 = motorwayMap.createList();
		Set<String> set1 = motorwayMap.createSet();
		_testList.add(motorwayMap.createTestRecord("Test1", list1, set1, 2, 2,
				2, true));
		_testList.add(null);
		city1.set_testList(_testList);
		List<TestRecord> compareList = new ArrayList<TestRecord>();
		List<String> list2 = motorwayMap.createList();
		Set<String> set2 = motorwayMap.createSet();
		compareList.add(motorwayMap.createTestRecord("Test1", list2, set2, 2,
				2, 2, true));
		compareList.add(null);
		System.out.println(compareList.equals(_testList));
		System.out.println(_testList.equals(compareList));

		MotorwayMapSchema schema = MotorwayMapSchema.instance();
		motorwayMap = schema.createMotorwayMapWithTransactionSupport();
		motorwayMap.newTransaction();
		motorwayMap.createCity();
		JGraLabSet<String> set = motorwayMap.createSet();
		set.add("t1");
		Savepoint sp = motorwayMap.defineSavepoint();
		set.add("t2");
		System.out.println(set.size());
		try {
			motorwayMap.restoreSavepoint(sp);
		} catch (InvalidSavepointException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(set.size());
	}

}
