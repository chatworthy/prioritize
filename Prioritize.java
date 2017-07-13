package apriori;

import java.io.*;
import java.util.*;

public class Prioritize {

	
	/**
	 * @param args
	 */
	
	static ArrayList<String>  itemNames;
  static ArrayList<String>  itemEntries;
  static ArrayList<String>  itemStates;
  static ArrayList<String>  itemStartDates;
  static ArrayList<String>  itemEndDates;
  static ArrayList<String>  itemDueDates;
  static ArrayList<Integer> itemPriorities;
  static ArrayList<String>  blogLines;
  static int numPriorityItems = 0;
  static int showBlogItems = 20;
  static Locale currentLocale = new Locale("en","US");
  static Calendar cal;
  static Date now;

  static Prioritize p;
  
  static boolean dirty;
  static boolean safeToDrop;
    
	public static void main(String[] args) {
		p = new Prioritize();
		p.init();
		p.run();
	}

	public void init() {
	  itemNames      = new ArrayList<String>();
	  itemPriorities = new ArrayList<Integer>();
    itemEntries    = new ArrayList<String>();
    itemStates     = new ArrayList<String>();
    itemStartDates = new ArrayList<String>();
    itemEndDates   = new ArrayList<String>();
    itemDueDates   = new ArrayList<String>();
    blogLines      = new ArrayList<String>();
    cal = Calendar.getInstance();
    now = cal.getTime();
    blogInit();
    dirty = false;
    safeToDrop = true;
    log("Startup",1);
	}
	
	public void run() {
	  String inputLine = "";
	  load();// load the priority list
	  list("active"); // show the priority list
	  System.out.println("TOP Priority is:");
	  list("top"); // show the top task
      while (!(inputLine.equals("quit"))) {
        // debug inputLine = apriori.Helper.getUserInput(dirty+" "+"cmd>");
        inputLine = apriori.Helper.getUserInput("cmd>");
    	if (!(inputLine==null)) {
      	    parse(inputLine);
    	} else {
    		inputLine = "";
    	}
      }
    }

    public void parse(String i) {
    	boolean parsed = false;
    	if (i==null) {parsed = true;}
    	if ((!parsed)&&(i.equals(""))) {parsed = true;}
    	if ((!parsed)&&(i.equals("help"))) {
    		System.out.println("help            - this help");
        System.out.println("activate (task) - mark task as active");
        System.out.println("add (task)      - add a task to the priority list");
        System.out.println("complete (task) - mark task as completed");
        System.out.println("daily (task)    - mark task as daily");
    		System.out.println("drop (task)     - remove a task from the priority list");
        System.out.println("list            - list all tasks in priority order");
        System.out.println("list all        - list all tasks in priority order");
        System.out.println("list active     - list active/daily tasks in priority order");
        System.out.println("list daily      - list daily tasks in priority order");
        System.out.println("list done       - list completed tasks");
        System.out.println("list dormant    - list dormant tasks");
        System.out.println("list inactive   - list dormant and completed tasks");
        System.out.println("run             - run the prioritizer");
        System.out.println("show            - synonym for list, can use all list options");
        System.out.println("suspend (task)  - mark task as dormant");
        System.out.println("top             - list the first task");
    		System.out.println("quit            - exit the program");
    		parsed = true;
    	}
    	// parse commands with no arguments
      if ((!parsed)&&(i.equals("show log"))) {
        reportBlog(showBlogItems);
        parsed = true;
      }
      if ((!parsed)&&(i.equals("list log"))) {
        reportBlog(showBlogItems);
        parsed = true;
      }
    	if ((!parsed)&&(i.equals("quit"))) {
    		System.out.println("Quitting...");
    		parsed = true;
        //dirty = true;
        //log("Quit\n\n");
    		cleanup();
    	}
      if ((!parsed)&&(i.equals("export"))) {
        exportAll();
        parsed = true;
      }
      if ((!parsed)&&(i.equals("list inactive"))) {
        list("inactive");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("list dormant"))) {
        list("dormant");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("list daily"))) {
        list("daily");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("list active"))) {
        list("active");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("list done"))) {
        list("done");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("list dormant"))) {
        list("dormant");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("list all"))) {
        list("all");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("list"))) {
        list("all");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("show inactive"))) {
        list("inactive");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("show dormant"))) {
        list("dormant");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("show daily"))) {
        list("daily");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("show active"))) {
        list("active");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("show done"))) {
        list("done");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("show dormant"))) {
        list("dormant");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("show all"))) {
        list("all");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("show"))) {
        list("all");
        parsed = true;
      }
      if ((!parsed)&&(i.equals("top"))) {
        list("top");
        parsed = true;
      }
      // parse commands with arguments
      String[] i2 = i.split(" ");
      if ((!parsed)&&(i2[0].equals("activate"))) {
        activate(i);
        parsed = true;
      }
      if ((!parsed)&&(i2[0].equals("add"))) {
        add(i);
        parsed = true;
      }
      if ((!parsed)&&(i2[0].equals("complete"))) {
        complete(i);
        parsed = true;
      }
      if ((!parsed)&&(i2[0].equals("daily"))) {
        daily(i);
        parsed = true;
      }
      if ((!parsed)&&(i2[0].equals("drop"))) {
        drop(i);
        parsed = true;
      }
      if ((!parsed)&&(i2[0].equals("log"))) {
        blog(i);
        parsed = true;
      }
    	if ((!parsed)&&(i2[0].equals("run"))) {
    		//System.out.println(i+": not implemented");
    		runPriority();
    		parsed = true;
    	}
      if ((!parsed)&&(i2[0].equals("setvar"))) {
        //System.out.println("DEBUG: setvar pre: i:["+i+"]");
        setVarTo(i);
        parsed = true;
      }
      if ((!parsed)&&(i2[0].equals("suspend"))) {
        suspend(i);
        parsed = true;
      }
    	if (!parsed) {
        System.out.println(i + ": not understood");
        log(i + ": not understood");
    	}
    }

/*************** Verbs ***************/     

    public void list(String param) {
      safeToDrop = true; // this indicates that the most recent
                         // priority list has been shown
      int lowPrint = 0;
      int highPrint = 0;
      int thisPrint = 0;
      boolean onlyOne = false;
      if (param.equals("active"))   { highPrint=2; }
      if (param.equals("all"))      { highPrint=4; }
      if (param.equals("done"))     { highPrint=4; lowPrint=4; }
      if (param.equals("dormant"))  { highPrint=3; lowPrint=3; }
      if (param.equals("daily"))    { highPrint=2; lowPrint=2; }
      if (param.equals("inactive")) { highPrint=4; lowPrint=3; }
      if (param.equals("top"))      { highPrint=2; onlyOne=true;}
      int pre = 0;
      if (numPriorityItems > 0) {
        System.out.println();
      	//System.out.println((numPriorityItems)+" items:");
      	if (!(itemEntries.isEmpty())) {
          for (int t=itemEntries.size()-1; t>=0; t--) {
            itemEntries.remove(t);	  
          }
      	}
      	for (int t=0; t<numPriorityItems; t++) {
          String i = itemNames.get(t);  
          String k = itemStates.get(t);  
        	Integer j = itemPriorities.get(t);
        	j=j+10000;
        	itemEntries.add(j +"::" + i + "::" + k + "::" + t);  
        }
      	Collections.sort(itemEntries);
      	for (int t=0; t<numPriorityItems; t++) {
      	  String i = itemEntries.get(t);
      	  String[]j = i.split ("::");
      	  if(j.length>1) {
            if (j[2].equals("Active"))   { thisPrint = 1; }         
            if (j[2].equals("Daily"))    { thisPrint = 2; }         
            if (j[2].equals("Dormant"))  { thisPrint = 3; }         
            if (j[2].equals("Finished")) { thisPrint = 4; }
            if ((thisPrint<=highPrint)&&(thisPrint>=lowPrint)) {
      	      String g = ((j[2]+"    ").substring(0,8));
      	      if ((!(thisPrint==pre))&&(thisPrint>2)) {
                System.out.println();
                pre = thisPrint;
      	      }
      	      String k = j[3];
      	      String h = (("   "+k).substring(k.length()));
              System.out.println(h+": ["+g+"] "+j[1]); //+" ("+j[0]+")");  
              //System.out.format(h+": ["+g+"] "+j[1]); //+" ("+j[0]+")");  
            }
            if (onlyOne) { t = numPriorityItems + 1; } // break us out
      	  }
      	}
        System.out.println();
      } else {
        System.out.println("No items to prioritize.");  
        log("No items to prioritize.");  
      }
    }
	
    public void add(String s) {
      String t=s.replaceFirst("add ","").trim();
      addOne(t,"1000");
   	  System.out.println("Added: "+t);  
      dirty = true;
      log("add:"+t);
	  saveAll();
    }
	
    public void drop(String s) {
      String t;
      int q, z;
      t=s.replaceFirst("drop ","").trim();
      z=-1;
      if (!safeToDrop) {
        try {
          z = Integer.parseInt(t);
        }
        catch (Exception x) {}
      }
      if ((!safeToDrop)&&(z>=0)) {
        System.out.println("ITEM NUMBERS HAVE CHANGED.\nDo a list or show command before dropping an item");  
        q=-1;
      } else {
        q=getItemIDByName(t);
    	  if (q>=0) {
          itemNames.remove(q);
          itemStates.remove(q);
    	    itemPriorities.remove(q);	
      	  numPriorityItems--;
         	System.out.println("Dropped: "+t);  
          dirty = true;
          safeToDrop = false;
          log("drop:"+t);
        saveAll();
    	  } else {
          System.out.println("drop: \""+t+"\" not found.");  
          log("drop: \""+t+"\" not found.");  
    	  }
      }
    }

    public void activate(String s) {
      String t=s.replaceFirst("activate ","").trim();
      int q=getItemIDByName(t);
      if (q>=0) {
        itemStates.set(q,"Active");
        log("active:"+t);
        System.out.println("Activated: "+t);  
        dirty = true;
        saveAll();
      } else {
        System.out.println("activate: \""+t+"\" not found.");  
      }
    }

    public void complete(String s) {
      String t=s.replaceFirst("complete ","").trim();
      String f = "";
      int q=getItemIDByName(t);
      if (q>=0) {
        f = itemNames.get(q);
        itemStates.set(q,"Finished");
        itemPriorities.set(q, 2000);
        itemEndDates.set(q,new Date().toString());
        System.out.println("Completed: "+f);  
        dirty = true;
        log("done:"+f);
        blog("donelog : "+f); // has a Log command for blog to consume
        saveAll();
      } else {
        System.out.println("complete: \""+t+"\" not found.");  
        log("complete: \""+t+"\" not found.");  
      }
    }

    public void daily(String s) {
      String t=s.replaceFirst("daily ","").trim();
      int q=getItemIDByName(t);
      if (q>=0) {
        itemStates.set(q,"Daily");
        System.out.println("Daily: "+t);  
        dirty = true;
        log("daily:"+t);
        saveAll();
      } else {
        System.out.println("daily: \""+t+"\" not found.");  
        log("daily: \""+t+"\" not found.");  
      }
    }

    public void exportAll() {
      String rn, rs, st, et, txtOut, s;
      Integer rc;
      try { 
      BufferedWriter exf = new BufferedWriter(new FileWriter("pNotes.txt"));
      // export pri
      exf.write("EXFile:items.txt\n");
      for (int t=0; t<numPriorityItems; t++) {
        rn = itemNames.get(t); 
        rs = itemStates.get(t); 
        rc = itemPriorities.get(t); 
        try {
          st = itemStartDates.get(t);
        } catch (Exception eg) {
          st = "(startDate)";
        }
        try {
          et = itemEndDates.get(t);
        } catch (Exception eh) {
          et = "(endDate)";
        }
        exf.write(rn+"=="+rs+"::"+rc+"::"+st+"::"+et+"::(dueTime)\n");
      }
      exf.close();
      } catch (Exception ef) {
        System.out.println("Error in exportAll:items.txt");
        System.out.println(ef);
      }
      // export logs
      try { 
      BufferedWriter exf = new BufferedWriter(new FileWriter("pNotes.txt", true));
      exf.write("EXFile:priorityBlog.txt\n");
      for (int skp=0; skp<blogLines.size(); skp++) {
        txtOut = blogLines.get(skp);
        exf.write(txtOut+"\n");
      }
      exf.close();
      } catch (Exception ef) {
        System.out.println("Error in exportAll:priorityBlog.txt");
        System.out.println(ef);
      }
      // export source Helper
      try { 
      BufferedReader ifile = new BufferedReader(new FileReader("Helper.java"));
      BufferedWriter exf = new BufferedWriter(new FileWriter("pNotes.txt", true));
      exf.write("EXFile:Helper.java\n");
      while ((s = ifile.readLine())!= null) {
        exf.write(s+"\n");
      }
      exf.close();
      ifile.close();
      } catch (Exception ef) {}
      // export source Prioritize (wants to be a read/write)
      try { 
      BufferedReader ifile = new BufferedReader(new FileReader("Prioritize.java"));
      BufferedWriter exf = new BufferedWriter(new FileWriter("pNotes.txt", true));
      exf.write("EXFile:Prioritize.java\n");
      while ((s = ifile.readLine())!= null) {
        exf.write(s+"\n");
      }
      exf.close();
      ifile.close();
      } catch (Exception ef) {}
      try {
        BufferedReader lr = new BufferedReader(new FileReader("priorityLog.txt"));
        BufferedWriter exf = new BufferedWriter(new FileWriter("pNotes.txt", true));
        exf.write("EXFile:priorityLog.txt\n");
        while ((s = lr.readLine())!= null) {
          exf.write(s+"\n");;
        }
        exf.close();
        lr.close();
      }
      catch (Exception el) {}
    }

    public void importAll() {
      
    }

    public void suspend(String s) {
      String t=s.replaceFirst("suspend ","").trim();
      int q=getItemIDByName(t);
      if (q>=0) {
        itemStates.set(q,"Dormant");
        System.out.println("Suspended: "+t);  
        itemPriorities.set(q, 1500);
        dirty = true;
        log("suspend:"+t);
        saveAll();
      } else {
        System.out.println("suspend: \""+t+"\" not found.");  
        log("suspend: \""+t+"\" not found.");  
      }
    }

    public void runPriority() {
      if (numPriorityItems>0) {	
        for (int t=0; t<numPriorityItems; t++) {
          String k0=itemStates.get(t);
          if ((k0.equals("Active"))||(k0.equals("Daily"))) {
            itemPriorities.set(t, 1000);  
          }
          if (k0.equals("Dormant")) {
            itemPriorities.set(t, 1500);  
          }
          if (k0.equals("Finished")) {
            itemPriorities.set(t, 2000);  
          }
        }
        String pil = "";	
        boolean inputOK = false;
        for (int t=0; t<numPriorityItems; t++) {
          String k1=itemStates.get(t);
          for (int u=t+1; u<numPriorityItems; u++) {
          //   prompt with a and b
        	inputOK = false;
        	  String k2=itemStates.get(u);
            if (((k1.equals("Active"))||(k1.equals("Daily")))&&
                ((k2.equals("Active"))||(k2.equals("Daily")))) {
              System.out.println();
              System.out.println();
              System.out.println("1. "+itemNames.get(t));
              System.out.println("or");
              System.out.println("2. "+itemNames.get(u));
              while (!inputOK) {
          	    pil = apriori.Helper.getUserInput("Pick 1 or 2: ");
          	    //System.out.println("DEBUG: entered ["+pil+"]");
                if (!(pil==null)) {
                	if (pil.equals("1")) {
                	  itemPriorities.set(t, (itemPriorities.get(t)-1));	
               	  inputOK = true;
                	}
                	if (pil.equals("2")) {
                    itemPriorities.set(u, (itemPriorities.get(u)-1));	
                    inputOK = true;
                  }
                	if (pil.equals("q")) {
                    inputOK = true;
                  }
                } else {
              	pil = "";
                }
              }
            }
          }
        }
        dirty = true;
        list("active");
        System.out.println("TOP Priority is:");
        list("top");
        log("run\n");
	    saveAll();
	    savePrioritized();
      } else {
    	System.out.println("Item list is empty.");  
      }
    }

    public void setVarTo(String foo) {
      boolean done = false;
      String errata = " log lines";
      //System.out.println("DEBUG: setvar 00: foo:["+foo+"]");
      String s=foo.replaceFirst("setvar ","").trim();
      //System.out.println("DEBUG: setvar 01: s:["+s+"]");
      //if (s.contains(" ")) { 
        String[] t = s.split (" ");
        //for (int d=0; d<t.length; d++) {
        //  System.out.println("DEBUG: setvar 02: t["+d+"]:["+t[d]+"]");
        //}
        if (t.length>1) {
          if ((t[0].contains("show"))&&(t.length==2)) {
            int tmp = 0;
            try {
              tmp = Integer.parseInt(t[1]);
            } 
            catch (Exception e) {
              tmp = showBlogItems;
              errata = errata + " - unchanged - \""+t[1]+"\" is not a number";
            }
            //System.out.println("DEBUG: setvar 03: tmp:["+tmp+"]");
            if (tmp<5) {
              tmp = 5;
              errata = errata + " - minimum is 5";
            }
            showBlogItems = tmp;
            System.out.println("show: "+showBlogItems+errata);
            done = true;
          }
        } //t.length > 1
      //} else {
        if (!done) {
        System.out.println("Settings:");
        System.out.println("show: "+showBlogItems+" log lines");
        System.out.println("");
        }
      //}
    }

    public void showLog(int logLines) {
      String s;
      try {
        BufferedReader lr = new BufferedReader(new FileReader("priorityLog.txt"));
        while ((s = lr.readLine())!= null) {
          if (s.contains(": log")) {
          System.out.println(s);
          }
        }
        lr.close();
      }
      catch (Exception el) {}
    }
   
 /*************** Helpers ***************/     

    public void load() {
      String s = "";
      try {
    	BufferedReader itemfile = new BufferedReader(new FileReader("items.txt"));
    	while ((s = itemfile.readLine())!= null) {
    	  if (s.contains("::")) {	
      	    String[] t = s.split ("::");
      	    //System.out.print(t.length+": ");
      	    if(t.length>2) {
              addOne(t[0], t[1], t[2], t[3], t[4]);
              //System.out.println("ADDED:"+t[0]+", "+t[1]+", "+t[2]+", "+t[3]+", "+t[4]);
      	    } else {
      	      addOne(t[0], t[1]);
              //System.out.println("added:"+t[0]+", "+t[1]);
      	    }
    	  } else {
      	    addOne(s, "1000");
    	  }
    	}
    	itemfile.close();
    	dirty = false;
      log("load");
      } catch (Exception ef) {
        System.out.println("Error in load()");
        System.out.println(ef);
      }
    }

    public void saveAll() {
      String rn, rs, st, et;
      Integer rc;
      log("saveAll: dirty:"+dirty);
      if (dirty) {
        backup();
      try {	
    	BufferedWriter itemfile = new BufferedWriter(new FileWriter("items.txt"));
    	for (int t=0; t<numPriorityItems; t++) {
        //System.out.print("Saving item "+t+"/"+numPriorityItems+": ");
        rn = itemNames.get(t); 
        //System.out.print(rn+" ");
        rs = itemStates.get(t); 
        //System.out.print(rs+" ");
    	  rc = itemPriorities.get(t);	
        //System.out.print(rc+" ");
        try {
          st = itemStartDates.get(t);
        } catch (Exception eg) {
          st = "(startDate)";
          //System.out.println("itemStartDate missing for item "+t+": "+rn);
          //System.out.println(eg);
        }
        //System.out.print(st+" ");
        try {
          et = itemEndDates.get(t);
        } catch (Exception eh) {
          et = "(endDate)";
          //System.out.println("itemEndDate missing for item "+t+": "+rn);
          //System.out.println(eh);
        }
        //System.out.print(et+" ");
        //System.out.println(" ");
    	  itemfile.write(rn+"=="+rs+"::"+rc+"::"+st+"::"+et+"::(dueTime)\n");
    	}
    	itemfile.close();
    	dirty = false;
      log("save");
      } catch (Exception ef) {
        System.out.println("Error in SaveAll");
        System.out.println(ef);
        log("Error in SaveAll");
      }
      } else { //if (dirty)
        log("Not saved - no changes");
      } //if (dirty)
    }
    
    public void savePrioritized() {
      try { 
      Date isNow = new Date();
      BufferedWriter itemfile = new BufferedWriter(new FileWriter("prioritized.txt"));
      itemfile.write("Prioritized "+isNow.toString()+"\n");
      for (int t=0; t<numPriorityItems; t++) {
        String i = itemEntries.get(t);
        String[]j = i.split ("::");
        if(j.length>1) {
          if ((j[2].equals("Active"))||(j[2].equals("Daily")))    {
            String g = ((j[2]+"    ").substring(0,8));
            itemfile.write("["+g+"]  "+j[1]+"\n");  
          }
        }
      }
      itemfile.close();
      log("SavePri");
      } catch (Exception ef) {}
    }

    public void addOne(String ts_name, String ts_cap) {
	  String[] t=ts_name.split("==");
	  if (!(itemExists(t[0]))) {
      Date dt = new Date();
	    itemNames.add(t[0]);
		  itemPriorities.add(Integer.parseInt(ts_cap));
      numPriorityItems = numPriorityItems + 1;
		  if (t.length>1) {
		    itemStates.add(t[1]);
	      itemStartDates.add(dt.toString());
		  } else {
        itemStates.add("Active");
        itemStartDates.add(dt.toString());
		  }
	  }
    }

    public void addOne(String ts_name, String ts_cap, String ts_StartDt,
                       String ts_endDt, String ts_dueDt) {
    //ts_name==ts_cap::1000::Wed Jun 28 13:29:44 MST 2017::(endTime)::(dueTime)
    String[] t=ts_name.split("==");
    if (!(itemExists(t[0]))) {
      Date dt = new Date();
      itemNames.add(t[0]);
      itemPriorities.add(Integer.parseInt(ts_cap));
      numPriorityItems = numPriorityItems + 1;
      if (t.length>1) {
        String[] u=t[1].split("::");
        itemStates.add(u[0]);
        if (ts_StartDt.equals("(startTime)")) {
          itemStartDates.add(dt.toString());
        } else {
          itemStartDates.add(ts_StartDt);
        }
        itemEndDates.add(ts_endDt);
        itemDueDates.add(ts_dueDt);
      } else {
        itemStates.add("Active");
        itemStartDates.add(dt.toString());
      }
    }
    }

    public boolean itemExists(String ts_name) {
	  boolean retval = false;
	  for (int t = 0; t<numPriorityItems; t++) {	
	    String q = itemNames.get(t);
		if (q.equals(ts_name)) {
		  retval = true;
		}
      }
      return retval;
    }

	public int getItemIDByName(String ts_name) {
	  //System.out.println("getItemIDByName("+ts_name+"): START");
	  int retval = -1;
	  for (int t = 0; t<numPriorityItems; t++) {	
		String q = itemNames.get(t);
		//System.out.println((t)+": "+q+" vs "+ts_name);
		if (q.equals(ts_name)) {
		  retval = t;
		  //System.out.println("Found");
		}
	  }
	  // But - we might have received a number, in which case
	  //       we need to parse it and make sure such an entry exists 
	  if (retval<0) {
	   int z = -1;
	    try {
	      z = Integer.parseInt(ts_name);
	    }
	    catch (Exception x) {}
      //System.out.print("getIDByName:"+ts_name+" => "+z); //+": "+q );
	    if ((z<numPriorityItems)&&(z>= 0)) {
	      String q = itemNames.get(z);
	      log("Item "+z+": "+q);
	      //System.out.print(": "+q );
	    } else {
	      z = -1;
	    }
      //System.out.println();
	    retval = z;
	  }
	  //System.out.println("Returning "+retval);
	  //System.out.println("getItemIDByName("+ts_name+"): END");
	  return retval;
	}

  public void backup() {
    int backupLevels = 5;
    String s;
    for (int t=backupLevels; t>0; t--) {
      try {
        BufferedReader ifile = new BufferedReader(new FileReader("itemsBak"+(t-1)+".txt"));
        BufferedWriter ofile = new BufferedWriter(new FileWriter("itemsBak"+(t)+".txt"));
        while ((s = ifile.readLine())!= null) {
          ofile.write(s+"\n");
        }
        ofile.close();
        ifile.close();
      }
      catch (Exception k) {}
    }
    try {
      BufferedReader ifile = new BufferedReader(new FileReader("items.txt"));
      BufferedWriter ofile = new BufferedWriter(new FileWriter("itemsBak0.txt"));
      while ((s = ifile.readLine())!= null) {
        ofile.write(s+"\n");
      }
      ofile.close();
      ifile.close();
      log("Backup");
    }
    catch (Exception j) {}
  }
  
  public void reportBlog() {
    reportBlog(showBlogItems);
  }
  
  public void reportBlog(int linesToReport) {
    int linesLeft = linesToReport;
    int linesAvailable = blogLines.size();
    if (linesAvailable<=linesToReport) {
      System.out.println("Showing all "+linesAvailable+" log entries");
    } else {
      System.out.println("Showing latest "+linesToReport+" of "+linesAvailable);
    }
    for (int skp=linesAvailable-1; skp>=0; skp--) {
      if (linesLeft>0) {
      System.out.println(blogLines.get(skp));
      }
      linesLeft--;
    }
  }
  
  public void log(String t) {
    log(t,0);
  }

  public void log(String t,int skipLines) {
    try {
      FileWriter lw = new FileWriter("priorityLog.txt", true);
      BufferedWriter lbw = new BufferedWriter(lw);
      PrintWriter logW = new PrintWriter(lbw);
      Date d = new Date();
      for (int skp=0; skp<skipLines; skp++) {
        logW.println();
      }
      logW.println(d.toString()+": "+t);
      logW.close();
    }
    catch (Exception el) {}
  }

  public void blogInit() {
    // init the blog arraylist here    
    String s = "";
    try {
      BufferedReader ifile = new BufferedReader(new FileReader("priorityBlog.txt"));
      while ((s = ifile.readLine())!= null) {
        blogLines.add(s);
      }
      ifile.close();
    }
    catch (Exception el) {}
    //System.out.println("blog: "+(blogLines.size())+ " lines");
  }
  
  public void blog(String t) {
    String txtOut = "";
    String s = "";
    try {
      FileWriter lw = new FileWriter("priorityBlog.txt");
      BufferedWriter lbw = new BufferedWriter(lw);
      PrintWriter logW = new PrintWriter(lbw);
      Date d = new Date();
      s=d.toString()+": "+t;
      blogLines.add(s.replaceFirst("log ","").trim());
      //System.out.println("blog: "+s);
      for (int skp=0; skp<blogLines.size(); skp++) {
        txtOut = blogLines.get(skp);
        logW.println(txtOut);
      }
      logW.close();
    }
    catch (Exception el) {}
    //System.out.println("blog: "+(blogLines.size())+ " lines");
  }
  
	public void cleanup() {
		System.out.print("Cleaning up...");
		exportAll();
		saveAll();
    log("Quit\n\n");
		System.out.println("Done");
  }


}

