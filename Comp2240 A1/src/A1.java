/*
 * @author c3338047 Yuji
 */

import java.util.*;
import java.io.*;

public class A1
{
    private ArrayList <Process> originalQueue = new ArrayList<>();  //store the original values, and should Not be the final as updating some set values
    private ArrayList <Process> readyGoQueue = new ArrayList<>(); //ready and go the data
    private int disp = 0, size; //update the disp value when it scans the files
    private StringBuffer average = new StringBuffer();

    /*
     *pre-condition: none
     *post-condition: importing files
     */

    private void importFile(String fileName) //given file name comes here
    {
        String importName = null;
        Scanner importStream = null;
        String new_iD = "unknown"; //give default values just in case
        int new_arrival = -999, new_excSize = -999, new_priority = -999; //ready to store the value into the array

        try //this try is try to access the file
        {
            System.out.println("Looks Like You Gave Me a File Name.");
            System.out.println("...Importing...");
            importName = fileName;
            importStream = new Scanner(new File(importName));

            while(importStream.hasNextLine())
            {
                try //this try is try to scan the contexts
                {
                    String line = importStream.nextLine(); //reading lines
                    if(line.equals("")) //to preventing to stop run when the begining of the line is nothing
                    {
                        continue;
                    }

                    String[] parts = line.split(" "); //sprit the lines by space

                    if(parts[0].equalsIgnoreCase("DISP:"))
                    {
                        disp = Integer.parseInt(parts[1]); //updating disp value
                        continue;
                    }

                    if(parts[0].equalsIgnoreCase("ID:"))
                    {
                        new_iD = parts[1];
                        continue;
                    }

                    if(parts[0].equalsIgnoreCase("Arrive:"))
                    {
                        new_arrival = Integer.parseInt(parts[1]);
                        continue;
                    }

                    if(parts[0].equalsIgnoreCase("ExecSize:"))
                    {
                        new_excSize = Integer.parseInt(parts[1]);
                        continue;
                    }

                    if(parts[0].equalsIgnoreCase("Priority:"))
                    {
                        new_priority = Integer.parseInt(parts[1]);
                        continue;
                    }

                    if(!new_iD.equals("unknown") && parts[0].equalsIgnoreCase("END"))
                    {
                        originalQueue.add(new Process(new_iD, new_arrival, new_excSize, new_priority)); //store the first data in to the process 1 array
                        readyGoQueue.add(new Process(new_iD, new_arrival, new_excSize, new_priority)); //copying the data
                    }
                } //end of try to read

                catch(ArrayIndexOutOfBoundsException a)
                {
                    System.out.println("Invalid Line Format: Not Enough Information");
                }
                catch(NoSuchElementException | NullPointerException n)
                {
                    System.out.println(n.getMessage());
                }
            } // end of while loop
        } // end of try to access the files

        catch(FileNotFoundException e) //catch for access files' errors
        {
            System.out.println("!!! Rage Mode !!! ");
            System.out.println("!!! Why you gave me wrong file name !!! ");
            System.out.println("After I count to 10, I'll be a nice girl");
            System.out.println("10, 9, 8, ... , 2, 1 ...");
            System.out.println("Error Opening The File " + importName);
        }
        finally //finally done to store the values from the text file
        {
            if(importStream !=null)
            {
                size = originalQueue.size();
                System.out.println("Importing is done. Let simulate the results =) "); //not necessary to show the nessage
                importStream.close(); //closing import stream for the next
            }
        }
    }

    /*
     *pre-condition: sorted array by arrival time and this condition is given
     *post-condition: calculate first in first out with non-parallel way.
     */

    private void FCFS() //first in first out
    {
        int time = 0, execution = 0, index =0, log = 0;
        StringBuffer psTurnAround = new StringBuffer();
        ArrayList <Process> cpu = new ArrayList<>(); //brand new cpu

        int fin = readyGoQueue.size(); //instead of while(!readyGoQueue is empty), in this case both are okay to use
        do
        {
            if(readyGoQueue.get(execution).getArrive() <= time) //if the processor arrives, then it should go to the cpu
            {
                time += disp; //dispatching processor to cpu
                cpu.add(readyGoQueue.get(execution)); //cpu gets a process
                readyGoQueue.remove(execution); //remove from ready queue
            }

            if(!cpu.isEmpty()) //if among the processes has the huge gap, this is prevent the errors
            {
                log = time; //this is time for cpu finish the process or cpu deals with process
                time += cpu.get(execution).getExcSize(); //calculate how much cpu took the time to finish the process
                int TA = time - cpu.get(execution).getArrive(); //Turn around time is current time - processor arrives time
                int FT = TA - cpu.get(execution).getExcSize(); //total waiting time is turn over - execution time
                originalQueue.get(index).setTime(TA, FT); //the values store into the original queue

                psTurnAround.append("T"+ log +": " + cpu.get(execution).getID() +"(" + cpu.get(execution).getPriority() + ")\n"); //adding the log in the string buffer
                cpu.remove(execution); //removing from the cpu
                index++; //the values are sorted by the arrival time, therefore just increment the index and all values from ready queue and original are matching automatically
                fin--;
            }

            if(!readyGoQueue.isEmpty() && readyGoQueue.get(execution).getArrive() > time) //this is killing the gaps
            {                                                                              //process A arrive time is 1 Process B is 100, after process A finishes Process B is still beating the time 100 > 1 + execution time + disp
                time++;                                                                    //then here is time is ticking
            }
        }while(fin > 0);
        
        printOut(originalQueue, psTurnAround);
        average(originalQueue, "FCFS            ");
    }

    /*
     *pre-condition: none
     *post-condition: calculate first in first out with non-parallel way.
     */

    private void SPN()
    {
        int time = 0, execution = 0, log = 0;
        StringBuffer psTurnAround = new StringBuffer();
        ArrayList <Process> cpu = new ArrayList<>(); //cpu is just accepts only 1 process
        ArrayList <Process> wait = new ArrayList<>();  // ready queue goes to wait first
        readyGoQueue.clear(); //just in case, this one does not need

        Iterator<Process> copy = originalQueue.iterator(); //coping from original queue
        while(copy.hasNext()) {
            Process q = copy.next();
            readyGoQueue.add(new Process(q.getID(), q.getArrive(),q.getExcSize(), q.getPriority()));
        }

        int fin = readyGoQueue.size();
        do
        {
            if(readyGoQueue.get(execution).getArrive() <= time) //the next process cannot dispatch at this moment == current process can finish it.
            {

                wait.add(readyGoQueue.get(execution)); //arrived process goes to wait list to sort by shortest service time first

                readyGoQueue.remove(execution); //remove the process to add the next ones

                while(!readyGoQueue.isEmpty() && readyGoQueue.get(execution).getArrive() <= time) //adding all arrived processes at the moment
                {
                    wait.add(readyGoQueue.get(execution));
                    readyGoQueue.remove(execution);
                }

                executionSort(wait); //assume at least one process is in the wait list, if here is caused of the array index bound, add if(!wait.isEmpty()) until line 204
                cpu.add(wait.get(execution)); //shortest the service time goes to the cpu
                time += disp; //dispatching
                wait.remove(execution);

                int waitlist = wait.size() -1;
                while(!wait.isEmpty()) //all processes in the wait list back to ready process because they are assured that the next execution due to the arrival time is smaller than current time
                {
                    readyGoQueue.add(0, wait.get(waitlist)); //back to the ready queue's head from the wait list's tail
                    wait.remove(waitlist);
                    waitlist--;
                } // here is end of the if(!wait.isEmpty()) if it is the necessary
            }

            if(!cpu.isEmpty()) //cpu manages the process from this line to line 223, if the issue related to the cpu, fix between the lines
            {
                log = time;
                for(int find =0; find < size; find++)
                {
                    if(cpu.get(execution).getID().equalsIgnoreCase(originalQueue.get(find).getID())) //finding the original process because of the sort
                    {
                        time += cpu.get(execution).getExcSize();
                        int TA = time - cpu.get(execution).getArrive();
                        int FT = TA - cpu.get(execution).getExcSize();
                        originalQueue.get(find).setTime(TA, FT);
                        break;
                    }
                }
                fin --;
                psTurnAround.append("T"+ log +": " + cpu.get(execution).getID() +"(" + cpu.get(execution).getPriority() + ")\n");
                cpu.remove(execution);
            } //end of the cpu task

            if(!readyGoQueue.isEmpty() && readyGoQueue.get(execution).getArrive() > time)
            {
                time++;
            }
        }while(fin > 0);// || cpu.size() > 0);

        printOut(originalQueue, psTurnAround);
        average(originalQueue, "SPN             ");
    }

    /*
     *pre-condition: none
     *post-condition: none.
     */

    private void PP()
    {
        int time = 0, execution = 0, log;
        StringBuffer psTurnAround = new StringBuffer();
        ArrayList <Process> cpu = new ArrayList<>(); //only 1 process accept
        ArrayList <Process> wait = new ArrayList<>(); //wait list and all readyGo queue process goes to here first

        readyGoQueue.clear(); //just in case, not necessary
        Iterator<Process> copy = originalQueue.iterator();

        while (copy.hasNext()) {
            Process q = copy.next();
            readyGoQueue.add(new Process(q.getID(), q.getArrive(),q.getExcSize(), q.getPriority()));
        }

        int runs = readyGoQueue.size(); //this is highly recommend to use the size this time

        do
        {
            if(!readyGoQueue.isEmpty() && readyGoQueue.get(execution).getArrive() <= time) //the next process cannot dispatch at this moment == current process can finish it.
            {

                wait.add(readyGoQueue.get(execution)); //these steps are as same as SPN and PRR

                readyGoQueue.remove(execution);

                while(!readyGoQueue.isEmpty() && readyGoQueue.get(execution).getArrive() <= time)  //finding all arrived processes
                {
                    wait.add(readyGoQueue.get(execution));
                    readyGoQueue.remove(execution);
                }

                if(!wait.isEmpty()) //if size == 1, it is not necessary to do it
                {
                    prioritySort(wait); //this time does not matter how many processes are in the wait list, sorting by priority anyway
                }
                //if the highest priority does not come the first fix the priority function

                cpu.add(wait.get(execution)); //the highest priority goes to the cpu
                time += disp; //dispatching now
                wait.remove(execution);

                if(!wait.isEmpty()) //all processes in the wait list back to the ready queue because they are assured that the next trial (execution)
                {
                    int waitlist = wait.size() -1;
                    while(!wait.isEmpty())
                    {
                        readyGoQueue.add(0, wait.get(waitlist));
                        wait.remove(waitlist);
                        waitlist--;
                    }
                }
            }

            if(!cpu.isEmpty()) //cpu manages the process
            {
                log = time; //log the time anyway as cpu executes

                if(readyGoQueue.isEmpty() && wait.isEmpty()) //this is assumed that only one process is running == no comparison and interrupting
                {
                    time += cpu.get(execution).getExcSize();
                    cpu.get(execution).setExecSize(0);
                }
                else if(wait.isEmpty()) //these are between the first to BEFORE the end (n-1) if something wrong between 1 and n-1 fix this section
                {
                    //                             ready queue[HEAD] holds      *because before this step all queues go back to the ready queue
                // the arrived and priority sorted process   OR    not arrived yet process
                    if(cpu.get(execution).getPriority() > readyGoQueue.get(execution).getPriority()) //case1, current running process can being interrupted by not arrived queues
                    {
                        int gap = readyGoQueue.get(execution).getArrive() - time; //calculate the time
                        time += gap;
                        if(gap==0)
                        {
                            time += disp;
                            cpu.get(execution).setExecSize(cpu.get(execution).getExcSize() - disp); //here is not sure the value is disp, if the outcome is try with 1. (not sure which one is the correct)
                        }
                        else
                        {
                            cpu.get(execution).setExecSize(cpu.get(execution).getExcSize() - 1); //here is not sure about using 1 if the outcome is wrong try with disp
                        }
                        readyGoQueue.add(0, cpu.get(execution)); //current process in the cpu back to the ready queue
                    }
                    else if(cpu.get(execution).getPriority() < readyGoQueue.get(execution).getPriority()) //case2, any process in the ready queue which cannot interrupt the execution
                    {
                        time += cpu.get(execution).getExcSize();
                        cpu.get(execution).setExecSize(0);
                    }
                }

                if(cpu.get(execution).getExcSize() == 0) //finishing the process in the cpu
                {
                    for (int find = 0; find < size; find++)
                    {
                        if (cpu.get(execution).getID().equalsIgnoreCase(originalQueue.get(find).getID()))
                        {
                            int TA = time - cpu.get(execution).getArrive();
                            int FT = TA - originalQueue.get(find).getExcSize(); //cpu execution time is (0) decreased before this step. Using the the original value for the total waiting time
                            originalQueue.get(find).setTime(TA, FT);
                            break;
                        }
                    }
                    runs--;
                }

                psTurnAround.append("T"+ log +": " + cpu.get(execution).getID() +"(" + cpu.get(execution).getPriority() + ")\n");
                cpu.remove(execution);
            }

            if(!readyGoQueue.isEmpty() && !wait.isEmpty() && readyGoQueue.get(execution).getArrive() > time)
            {
                time++;
            }
        }while(runs > 0);

        printOut(originalQueue, psTurnAround);
        average(originalQueue, "PP              ");
    }

    /*
     *pre-condition: no queueTemp
     *post-condition: A variant of the standard Round Robin (RR) algorithm
     */

    private void PRR()
    {
        int time = 0, execution = 0, slicedTime = 0, log = 0;
        StringBuffer psTurnAround = new StringBuffer();
        ArrayList <Process> cpu = new ArrayList<>(); //brand new cpu
        ArrayList <Process> waitQueue = new ArrayList<>(); //usual wait list
        ArrayList <Process> firstArrived = new ArrayList<>(); //only first arrived process goes

        readyGoQueue.clear();

        Iterator<Process> copy = originalQueue.iterator();
        while (copy.hasNext())
        {
            Process q = copy.next();
            readyGoQueue.add(new Process(q.getID(), q.getArrive(),q.getExcSize(), q.getPriority()));
        }

        int runs = readyGoQueue.size(); //flag for the finish

        do
        {

           if(!readyGoQueue.isEmpty() && readyGoQueue.get(execution).getArrive() <= time)
           {
               if(readyGoQueue.get(execution).gettLog() == -999) //the default log value is -999 which means first arrived
               {
                   firstArrived.add(readyGoQueue.get(execution));
               }
               else
               {
                   waitQueue.add(readyGoQueue.get(execution)); //other processes go to the wait list
               }

               readyGoQueue.remove(execution);

               while(!readyGoQueue.isEmpty() && readyGoQueue.get(execution).getArrive() <= time) //collecting the all processes
               {
                   if(readyGoQueue.get(execution).gettLog() == -999) //if all processes arrived at the same time, they must stored in the first arrived to the tail
                   {
                       firstArrived.add(readyGoQueue.get(execution));
                   }
                   else
                   {
                       waitQueue.add(readyGoQueue.get(execution)); //for others
                   }
                   readyGoQueue.remove(execution);
               }

               if(!waitQueue.isEmpty()) //sort only wait list, first arrived is sorted by arrival time so, do not change the order
               {
                   logSort(waitQueue);
               }

               //section for deciding which process goes to the cpu. if the order is wrong fix here
               if(!firstArrived.isEmpty() && !waitQueue.isEmpty()) //case1, both queue has values
               {
                   if(waitQueue.get(execution).gettLog() < firstArrived.get(execution).getArrive()) //compare the last log value of the wait list and current arrived process
                   {
                       cpu.add(waitQueue.get(execution)); //if wait list log is smaller, then wait list can go to the cpu
                       waitQueue.remove(execution);
                   }
                   else
                   {
                       cpu.add(firstArrived.get(execution)); //otherwise, first arrive goes to the cpu
                       firstArrived.remove(execution);
                   }
               }

               // no comparison case
               else if(!firstArrived.isEmpty()) //case2, wait is empty and first arrived holds a process or many processes
               {
                   cpu.add(firstArrived.get(execution));
                   firstArrived.remove(execution);
               }
               else if(!waitQueue.isEmpty()) //first arrived is empty and (sorted by log time) wait list holds
               {
                   cpu.add(waitQueue.get(execution));
                   waitQueue.remove(execution);
               }

               //at the moment, we decided which process goes to cpu
               time += disp; //finally dispatching

               if(!waitQueue.isEmpty()) //back to ready queue
               {
                   //waitQueue.remove(execution);
                   int waitlist = waitQueue.size() - 1;
                   while (!waitQueue.isEmpty())
                   {
                       readyGoQueue.add(0, waitQueue.get(waitlist));
                       waitQueue.remove(waitlist);
                       waitlist--;
                   }
               }
               if(!firstArrived.isEmpty()) //back to the ready queue
               {
                   int first = firstArrived.size() - 1;
                   while (!firstArrived.isEmpty())
                   {
                       readyGoQueue.add(0, firstArrived.get(first));
                       firstArrived.remove(first);
                       first--;
                   }
               }
               //if current ready queue has weird orders, swap the backing order between the wait and first arrived
           }

           //cpu manges from this line to the end of this function

           if(cpu.get(execution).getPriority() <=2) //
           {
               slicedTime = 4;
           }
           else
           {
               slicedTime = 2;
           }

           log = time; // log the current time

           if(cpu.get(execution).getExcSize() - slicedTime <= 0) //case1, the process finished the task now
           {
               if(cpu.get(execution).getExcSize() - slicedTime == 0) //just 0, the current time is updating with the sliced time
               {
                   time += slicedTime;
               }
               else //the value went to negative
               {
                   time += cpu.get(execution).getExcSize( ); //actual task time is CURRENT cpu execution time
               }

               cpu.get(execution).setExecSize(0); //update the execution time

               for(int find = 0; find < size; find++)
               {
                   if(cpu.get(execution).getID().equalsIgnoreCase(originalQueue.get(find).getID()))
                   {
                       int TA = time - originalQueue.get(find).getArrive(); //cpu.get(execution).getArrive() also the same
                       int FT = TA - originalQueue.get(find).getExcSize(); //here MUST be the original values because CURRENT cpu execution is 0. 
                       originalQueue.get(find).setTime(TA, FT); //store the values to the original
                       break;
                   }
               }
               runs--;
           }

           else //cpu could not finish at the moment
           {
               time += slicedTime;
               cpu.get(execution).setLog(time);
               cpu.get(execution).setExecSize(cpu.get(execution).getExcSize() - slicedTime); //update cpu info
               readyGoQueue.add(0, cpu.get(execution));
           }

            psTurnAround.append("T"+ log +": " + cpu.get(execution).getID() +"(" + cpu.get(execution).getPriority() + ")\n");
            cpu.remove(execution);

            if(!readyGoQueue.isEmpty() && !waitQueue.isEmpty() && readyGoQueue.get(execution).getArrive() > time)
            {
                time++;
            }

        }while(runs > 0);
        
        printOut(originalQueue, psTurnAround);
        average(originalQueue, "PRR             ");
    }

    /*
     *pre-condition: none
     *post-condition: appending average of turnAround and wait time
     * Complexity is n
     */

    private void average(ArrayList<Process> new_Process, String name)
    {
        double averageTA = 0;
        double averageWait = 0;
        for(int index = 0; index < size; index++)
        {
            averageTA += new_Process.get(index).getTurnAround();
            averageWait += new_Process.get(index).getWait();
        }

        averageTA = averageTA / size;
        averageWait = averageWait / size;
        //String.format("%-.2f  ", averageWait);
        //averageWait = Math.round(averageWait * 100.0) / 100.0; //round up
        average.append(name + String.format("%-26.2f", averageTA) + String.format("%-20.2f", averageWait) + "\n"); //+ "                     "
    }

    /*
     *pre-condition: none
     *post-condition: print out the results.
     */

    private void printOut(ArrayList<Process> new_Process, StringBuffer out)
    {
        System.out.println(out);
        System.out.println("Process "+ "Turnaround Time " + "Waiting Time");
        for(int index = 0; index < size; index++)
        {
            System.out.println(new_Process.get(index).getID()+ "      " + new_Process.get(index).getTurnAround() + "           "+ new_Process.get(index).getWait());
        }
    }

    private void printA()
    {
        System.out.println("\nSummary");
        System.out.println("Algorithm       "+ "Average Turnaround Time   " + "Average Waiting Time");
        System.out.println(average);
    }

    /*
     *pre-condition: need Proces Array data
     *post-condition: sorted by execution time (longer).
     * Complexity is n^2
     */

    private void executionSort(ArrayList<Process> cpu)
    {
        int start = cpu.size();
        for(int i = cpu.size() -1; i > 0; i--)
        {
            for(int followCount = 0; followCount < i; followCount++)
            {
                if(followCount +1 < start)
                {
                    if(cpu.get(followCount).getExcSize() > cpu.get(followCount + 1).getExcSize())
                    {
                        Collections.swap(cpu, followCount, followCount+1);
                    }

                    if(cpu.get(followCount).getExcSize() == cpu.get(followCount + 1).getExcSize())
                    {
                        int comp1 = Integer.parseInt(cpu.get(followCount).getID().substring(1));
                        int comp2 = Integer.parseInt(cpu.get(followCount + 1).getID().substring(1));
                        if(comp1 > comp2)
                        {
                            Collections.swap(cpu, followCount, followCount+1);
                        }
                    }
                }
            }
        }
    }


    /*
     *pre-condition: need Proces Array data
     *post-condition: sorted by execution time (longer).
     * Complexity is n^2
     */

    private void prioritySort(ArrayList<Process> cpu)
    {
        int start = cpu.size();
        for(int i = cpu.size() -1; i > 0; i--)
        {
            for(int followCount = 0; followCount < i; followCount++)
            {
                if(followCount +1 < start)
                {
                    if(cpu.get(followCount).getPriority() > cpu.get(followCount + 1).getPriority())
                    {
                        Collections.swap(cpu, followCount, followCount+1);
                    }

                    if(cpu.get(followCount).getPriority() == cpu.get(followCount + 1).getPriority())
                    {
                        int comp1 = Integer.parseInt(cpu.get(followCount).getID().substring(1));
                        int comp2 = Integer.parseInt(cpu.get(followCount + 1).getID().substring(1));
                        if(comp1 > comp2)
                        {
                            Collections.swap(cpu, followCount, followCount+1);
                        }
                    }
                }
            }
        }
    }

    /*
     *pre-condition: need Proces Array data
     *post-condition: sorted by execution time (longer).
     * Complexity is n^2
     */

    private void logSort(ArrayList<Process> cpu)
    {
        int start = cpu.size();
        for(int i = cpu.size() -1; i > 0; i--)
        {
            for(int followCount = 0; followCount < i; followCount++)
            {
                if(followCount +1 < start)
                {
                    if(cpu.get(followCount).gettLog() > cpu.get(followCount + 1).gettLog())
                    {
                        Collections.swap(cpu, followCount, followCount+1);
                    }

                    if(cpu.get(followCount).gettLog() == cpu.get(followCount + 1).gettLog())
                    {
                        int comp1 = Integer.parseInt(cpu.get(followCount).getID().substring(1));
                        int comp2 = Integer.parseInt(cpu.get(followCount + 1).getID().substring(1));
                        if(comp1 > comp2)
                        {
                            Collections.swap(cpu, followCount, followCount+1);
                        }
                    }
                }
            }
        }
    }


    private void run(String fileName) //here is actual main runs for security reasons
    {
        System.out.println("G'day, I'm AggRetsuko, How are you?");
        System.out.println("I will simulate FCFS, SPN, PP, and PRR.");
        importFile(fileName); //given name from Java cmd goes to importing function
        System.out.println("\nFCFS:");
        FCFS();
        System.out.println("\nSPN:");
        SPN();
        System.out.println("\nPP:");
        PP();
        System.out.println("\nPRR:");
        PRR();
        printA();
    }

    public static void main(String[] args)
    {
        A1 sim = new A1();
        String fileName = args[0]; //this is for java cmd and any file name can take and user can gives any file from the cmd
        sim.run(fileName); //passing the file name to the run
    }
}