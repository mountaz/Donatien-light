/* This software is a light version of Donatien, a program created 
 * for the comparison and matching of graphs and clustered graphs
 * Copyright (C)2010 Pierre Dragicevic and Mountaz Hascoët
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
package donatien.gui;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import donatien.gui.CheckBox.InnerShape;
import donatien.model.graph.Graph;
import donatien.model.graph.MasterGraph;

public class MasterLayerBar extends LayerBar {
	
	CheckBox check_new;

	public MasterLayerBar(Layer parent) {
		super(parent);
	}
	
	@Override
	public void addWidgets() {
		//super.addWidgets();
		check_new = new CheckBox("New", InnerShape.Plus, false, true, true, false, false, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createMaster();
			}
		});
		widgets.add(check_new);
	}
	
	@Override
	public void layout() {
		super.layout();
		int checky = (View.layerBarHeight - CheckBox.size)/2;
		int spacex = CheckBox.size + 4;
		check_new.bounds.setLocation(5 + spacex, checky);		
	}
	
	@Override
	protected void paintWidgets(Graphics2D g) {
		//if (!parent.enabled) {
			//check_new.paint(g);
		//} else {
			super.paintWidgets(g);
		//}
	}
	
	/**
	 * 
	 */
	public void createMaster() {

		View view = parent.parent;
		
		// Create graph
		Graph master = new MasterGraph(parent);
		parent.setGraph(master);
		float y0 = view.contentBounds.y;
		parent.pan(0, -y0);

		// Update widget and layers
		widgets.remove(check_new);
		super.addWidgets();
		super.layout();
		setBaseColor(getBaseColor()); // update widget colors
		view.addLayer(new MasterLayer(), 0);
		setTitle(getRandomName() + " (" + master.getNodes().size() + " nodes)");
		parent.enabled = true;
		
		repaint();
	}
		
	protected static String getRandomName() {
		//NameGenerator ng = new NameGenerator("languages.txt", "semantics.txt");
		//return ng.getRandomName("Dämonen", "Dämon", 1)[0];
		
		String[] names = new String[] {"Adla", "Adleida", "Adlesha", "Adleta", "Adviga", "Afanasiia", "Afanasiya", "Afimia", "Afonaseva", "Agafia", "Agafiia", "Agafiya", "Agafokliia", "Agafonika", "Agafya", "Agapiia", "Agasha", "Agashka", "Aglaia", "Aglaida", "Aglaya", "Agna", "Agnessa", "Agnia", "Agniia", "Agrafena", "Agrafina", "Agramakova", "Agripena", "Agripina", "Agrippa", "Agrippina", "Aitugan", "Aizdiakova", "Akillina", "Akiulina", "Aksana", "Aksinya", "Alasa", "Albena", "Albina", "Aleksandra", "Alena", "Alenka", "Alexandra", "Alexcia", "Alexia", "Alexis", "Alina", "Alma", "Alona", "Alyssa", "Alzbeta", "Amelfa", "Ampliia", "Ana", "Anastasia", "Anastasiia", "Anastasija", "Anatassia", "Andreea", "Andreeva", "Andreiana", "Andrievicha", "Anechka", "Aneska", "Anfiia", "Anfoma", "Anfusa", "Angelika", "Angelina", "Angusta", "Ania", "Animaida", "Animaisa", "Anina", "Anisia", "Anisiia", "Anisiya", "Anisya", "Anitchka", "Anitsa", "Anizka", "Anja", "Anje", "Anjelica", "Anjelika", "Anka", "Ann", "Anna", "Annastasija", "Antonidka", "Antonina", "Anusia", "Anya", "Anzhela", "Apfiia", "Apolinaria", "Apolinariia", "Apoloniada", "Apolosakifa", "Ariadna", "Arina", "Arkhipa", "Arkhippa", "Artemeva", "Artemiia", "Asenka", "Askitreia", "Askitriia", "Asya", "Augusta", "Avdeeva", "Avdiushka", "Avdotia", "Avgusta", "Avramova", "Baialyn", "Baibichia", "Bakhteiarova", "Balbara", "Barbara", "Bazhena", "Bedche", "Bela", "Beleka", "Belgukovna", "Belka", "Bella", "Belukha", "Benka", "Bezruchka", "Bezubaia", "Bezui", "Biana", "Biata", "Bibishkina", "Biiata", "Biriuta", "Blanka", "Blausa", "Bogdana", "Bogukhvala", "Bogumezt", "Bogumila", "Boguslava", "Bohdana", "Bohumile", "Boika", "Bolce", "Boldina", "Bolemila", "Boleslava", "Bolgarina", "Bolgarynia", "Bona", "Borisova", "Boriuta", "Bozena", "Bozhana", "Bozhitsa", "Bragina", "Branislava", "Branizlawa", "Bratomila", "Bratromila", "Bratrumila", "Bruna", "Budisla", "Budizla", "Budshka", "Budska", "Bukhval", "Calina", "Catarina", "Caterina", "Catherine", "Catina", "Catreen", "Catrin", "Catrina", "Catrinia", "Catriona", "Catryn", "Cecislava", "Charlotta", "Chebotova", "Chekhina", "Chekhyna", "Cheliadina", "Chemislava", "Chenka", "Chernavka", "Chernislava", "Chernka", "Chesislava", "Chimislava", "Chiona", "Chiudka", "Chobotova", "Chynica", "Ciernislava", "Clavdia", "Cyzarine", "Czarina", "Czeimislawa", "Dalida", "Daliunda", "Dama", "Danilova", "Daria", "Darina", "Daritsa", "Darja", "Daromila", "Darya", "Dasha", "Datja", "Davyd", "Davyzha", "Davyzheia", "Debora", "Deda", "Dedenia", "Dekava", "Dekhova", "Demidova", "Denicha", "Deretka", "Derska", "Derzhena", "Derzhka", "Desa", "Desha", "Despa", "Dessa", "Desta", "Detana", "Detava", "Deva", "Devka", "Devochka", "Devochkina", "Devora", "Dikana", "Dima", "Dimitra", "Dimut", "Dina", "Dinah", "Dinara", "Dmitreeva", "Dmitrieva", "Dmitrovna", "Dobegneva", "Dobislava", "Dobka", "Dobra", "Dobrava", "Dobreva", "Dobromila", "Dobroslava", "Dobrowest", "Dobryna", "Doda", "Domaslava", "Dominika", "Domka", "Domna", "Domnika", "Domnikiia", "Domnina", "Domona", "Dorofeia", "Doroteya", "Dosya", "Dounia", "Dozene", "Dozhene", "Draginia", "Dragomira", "Dragoslawa", "Dragushla", "Draia", "Drga", "Drosida", "Druzhinina", "Dubrava", "Dubravka", "Duklida", "Dunya", "Dunyasha", "Duscha", "Dusha", "Dusya", "Dvora", "Ecatarina", "Ecatrinna", "Eda", "Edviga", "Edviva", "Efdokia", "Effimia", "Efimia", "Efiopskaia", "Efrasiia", "Efrosenia", "Efrossina", "Ekatarina", "Ekaterina", "Ekatrinna", "Ekzuperiia", "Elacha", "Eleena", "Elen", "Eleni", "Elenya", "Elga", "Elgiva", "Eliaksha", "Elikonida", "Elina", "Elisava", "Elisaveta", "Elissa", "Elizabeth", "Elizarova", "Elizaveta", "Ella", "Ellena", "Ellina", "Elonka", "Elzbeta", "Elzhbeta", "Ennafa", "Epestemiia", "Epikhariia", "Epistima", "Eretiia", "Ermolina", "Erotiida", "Ertugana", "Esineeva", "Euafina", "Eufemia", "Eugenia", "Euprakseia", "Eupraksiia", "Eva", "Evanova", "Evdokeia", "Evdokia", "Evdokiia", "Evdokiya", "Evdokseia", "Evdoksiia", "Evelina", "Evfaliia", "Evfrasiia", "Evfroseniia", "Evfrosinya", "Evgenia", "Evgeniia", "Evgeniya", "Evgenya", "Evginia", "Evguenia", "Evpraksi", "Evpraksiia", "Evrosena", "Evseevskaia", "Evsegniia", "Evseveia", "Evseviia", "Evstoliia", "Evtropiia", "Faina", "Fanaila", "Fanya", "Fatianova", "Fausta", "Favsta", "Fayina", "Fedia", "Fedka", "Fedkina", "Fedora", "Fedoritsa", "Fedorka", "Fedorova", "Fedosia", "Fedosiia", "Fedosya", "Fedotia", "Fedotiia", "Fedya", "Feia", "Feiniia", "Fekla", "Feklitsa", "Fenia", "Feodora", "Feodosia", "Feodosiia", "Feoduliia", "Feofana", "Feoklita", "Feoktista", "Feona", "Feonilla", "Feopimta", "Feopista", "Feopistiia", "Feozva", "Ferfufiia", "Ferufa", "Fesalonikiia", "Fetenia", "Fetinia", "Fetiniia", "Fevronia", "Filikitata", "Filippiia", "Filitsata", "Filofei", "Filofinaia", "Filonilla", "Fimochka", "Fiva", "Fiveia", "Foimina", "Fokina", "Fomina", "Fotina", "Fotiniia", "Fovro", "Fovroneia", "Frolova", "Frosiniia", "Gadina", "Gaianiia", "Gala", "Galenka", "Gali", "Galina", "Galina", "Galine", "Galochka", "Galya", "Galyna", "Gamana", "Gana", "Gananiia", "Gandaza", "Ganna", "Gasha", "Gema", "Genka", "Georgieva", "Gertruda", "Ginechka", "Giurgevaia", "Gizheurann", "Gizla", "Glafira", "Glasha", "Glebovicha", "Glikeriia", "Glikeriya", "Glukeriia", "Glukheria", "Godava", "Golindukha", "Goltiaeva", "Golubitsa", "Gordislava", "Gorislava", "Gorshedna", "Gostena", "Gostenia", "Gostiata", "Gostimira", "Goulislava", "Govdela", "Gravriia", "Grekina", "Grekinia", "Grekyna", "Grifina", "Grigoreva", "Grigorevna", "Grigorieva", "Groza", "Gruba", "Grunya", "Grusha", "Halyna", "Helen", "Helena", "Helenka", "Helga", "Hema", "Henka", "Hinezka", "Hinica", "Hodawa", "Hora", "Horina", "Hosche", "Hostena", "Hruoza", "Iadviga", "Iakova", "Iakovleva", "Iakovlevskaia", "Iakun", "Iakunova", "Iakunovaia", "Ianevaia", "Ianisha", "Ianishe", "Ianka", "Iarche", "Iarena", "Iarina", "Iarogned", "Iaroia", "Iarokhna", "Iaroslava", "Iarshek", "Iasynia", "Ieliaia", "Iev", "Ievlia", "Ifrosenia", "Ignateva", "Ignatevskaia", "Igoshkova", "Iia", "Ilariia", "Ilia", "Ilina", "Ilya", "Inessa", "Inkena", "Inna", "Ioanna", "Iona", "Iosifova", "Iovilla", "Ira", "Iraida", "Irena", "Irene", "Irina", "Irinia", "Irinka", "Irisa", "Irodia", "Irodiia", "Isakova", "Isidora", "Ismagrad", "Itka", "Iudita", "Iuliana", "Iuliania", "Iulianiia", "Iuliia", "Iulita", "Iulitta", "Iuniia", "Iurevna", "Iustina", "Ivana", "Ivanova", "Ivanovskaia", "Iveska", "Ivonne", "Iziaslava", "Izmaragd", "Janna", "Jarena", "Jarene", "Jarohna", "Jekaterina", "Jelena", "Jelena", "Jelizaveta", "Jenica", "Jeremia", "Jevdokija", "Jitka", "Julia", "Kace", "Kacha", "Kache", "Kachka", "Kala", "Kaleria", "Kaleriia", "Kalia", "Kalisa", "Kalisfena", "Kalista", "Kalitina", "Kallisfeniia", "Kallista", "Kamenka", "Kamle", "Kandaza", "Kapetolina", "Kaptelina", "Karen", "Karina", "Karine", "Karinna", "Karolina", "Karpova", "Karpovskaia", "Karrine", "Karyna", "Kasha", "Kashka", "Kata", "Katalena", "Katareena", "Katarina", "Kateena", "Katerina", "Katerinka", "Katherina", "Katherine", "Katia", "Katina", "Katinka", "Katiya", "Katja", "Katlina", "Katreen", "Katreena", "Katrene", "Katria", "Katrien", "Katrina", "Katrine", "Katrusha", "Katrya", "Katryn", "Katryna", "Kattrina", "Kattryna", "Katunia", "Katuscha", "Katya", "Katyenka", "Katyushka", "Katyuska", "Kazdoia", "Kerkira", "Kharesa", "Khariessa", "Kharitaniia", "Kharitina", "Kharitona", "Kharitonova", "Kheoniia", "Khioniia", "Khlopyreva", "Khovra", "Khrana", "Khrisiia", "Khristeen", "Khristen", "Khristianova", "Khristin", "Khristina", "Khristine", "Khristyana", "Khristyna", "Khrstina", "Khrystina", "Khrystyn", "Khrystyne", "Khvalibud", "Khynika", "Kikiliia", "Kilikeia", "Kilikiia", "Kiprilla", "Kira", "Kiraanna", "Kiriakiia", "Kiriena", "Kirilla", "Kirilovskaia", "Kisa", "Kiska", "Kitsa", "Kittiana", "Kiuprila", "Kiuriakiia", "Kiza", "Klasha", "Klavdiia", "Kleopatra", "Klychikha", "Knikki", "Kogorshed", "Koia", "Koika", "Kolomianka", "Konchaka", "Konchasha", "Konkordiia", "Konstantiia", "Konstiantina", "Konstiantinova", "Kora", "Koretskaia", "Korina", "Korotkaia", "Korotkova", "Korotsek", "Korotskovaia", "Kosa", "Kosenila", "Kostenka", "Kostya", "Kostyusha", "Kotik", "Kovan", "Kovana", "Kowan", "Kozma", "Kozmina", "Krabava", "Krasa", "Krestiia", "Kristina", "Krivulinaia", "Krunevichovna", "Krushka", "Ksafipa", "Ksana", "Ksanfippa", "Ksanochka", "Ksenia", "Kseniia", "Kseniya", "Ksenya", "Kshtovtovna", "Ksnia", "Ksniatintsa", "Kudra", "Kuna", "Kunei", "Kunka", "Kunko", "Kunku", "Kuntse", "Kuriana", "Kuznetsova", "Kvasena", "Kvetava", "Kzhna", "Lacey", "Lacey", "Lada", "Laikina", "Lala", "Lanassa", "Lanka", "Lara", "Lari", "Larina", "Larisa", "Larissa", "Larissa", "Larochka", "Larra", "Laryssa", "Latskaia", "Leia", "Leka", "Lelik", "Lena", "Lenina", "Lenochka", "Lenora", "Lenusy", "Lenusya", "Leonilla", "Leonteva", "Lepa", "Lera", "Lerka", "Leva", "Liba", "Libania", "Libusa", "Lida", "Lidena", "Lidia", "Lidiia", "Lidija", "Lidiy", "Lidiya", "Lidka", "Lidmila", "Lidocha", "Lidochka", "Lieba", "Lila", "Lilac", "Lilia", "Liolya", "Lipa", "Lisa", "Lisanka", "Lisaveta", "Liseetsa", "Lishka", "Lisil", "Liska", "Lisotianka", "Liuba", "Liubchanina", "Liubka", "Liubokhna", "Liubone", "Liubusha", "Liudena", "Liudmila", "Liunharda", "Liutarda", "Liutsilla", "Liza", "Lizabeta", "Lizanka", "Lizette", "Ljudmila", "Ljudmilla", "Lolya", "Lotta", "Luba", "Lubachitsa", "Lubmila", "Lubmilla", "Lubohna", "Lubov", "Lubusha", "Luda", "Ludiia", "Ludmia", "Ludmila", "Ludmilla", "Ludomia", "Luka", "Lukeria", "Lukerina", "Lukerya", "Lukiia", "Lukina", "Lukiria", "Lukoianova", "Lvovicha", "Lyalechka", "Lyalya", "Lybed", "Lydia", "Lyeta", "Lyuba", "Lyubochka", "Lyubonka", "Lyubov", "Lyudmila", "Lyudmilla", "Lyuha", "Lyutsiana"};
		return names[(int)(Math.random() * names.length)];
	}
	
	@Override
	public boolean isNewMasterBar() {
		return parent.getGraph() == null;
	}

}
