package com.nurdiansyah;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner konsolInput = new Scanner(System.in);
        String userOption;
        boolean isNext = true;
        while (isNext) {
            clearScreen();
            System.out.println("Database List Order\n");
            System.out.println("1.\tList Order");
            System.out.println("2.\tFind Order");
            System.out.println("3.\tAdd order data");
            System.out.println("4.\tChange list order");
            System.out.println("5.\tDelete list order");

            System.out.print("\n\nInput option: ");
            userOption = konsolInput.next();

            switch (userOption) {
                case "1":
                    System.out.println("list order");
                    System.out.println("**********");
                    showData();
                    break;
                case "2":
                    System.out.println("find order");
                    System.out.println("**********");
                    findData();
                    break;
                case "3":
                    System.out.println("add order data");
                    System.out.println("**************");
                    addData();
                    showData();
                    break;
                case "4":
                    System.out.println("change list order");
                    System.out.println("*****************");
                    updateDataOrder();
                    break;
                case "5":
                    System.out.println("delete list order");
                    System.out.println("*****************");
                    deleteDataOrder();
                    break;
                default:
                    System.err.println("\nError input \nChosee option [1-5]");
            }
            isNext = getYesorNo("Do you want to continue your option");
        }
    }
    private static void clearScreen(){
        try {
            if (System.getProperty("os.name").contains("Windows")){
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (Exception ex){
            System.err.println("cannot clear screen");
        }
    }
    private static void updateDataOrder() throws IOException{
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);
        System.out.println("List Order");
        showData();

        Scanner konsolInput = new Scanner(System.in);
        System.out.print("\ninput number order will be updated: ");
        int updateNum = konsolInput.nextInt();

        String data = bufferedInput.readLine();
        int entryCounts = 0;

        while (data != null){
            entryCounts++;

            StringTokenizer st = new StringTokenizer(data,",");
            if (updateNum == entryCounts){
                System.out.println("\nYour update data order:");
                System.out.println("-----------------------------------");
                System.out.println("Order ID       : " + st.nextToken());
                System.out.println("Order date     : " + st.nextToken());
                System.out.println("Costumer nam   : " + st.nextToken());
                System.out.println("Country        : " + st.nextToken());

                String[] fieldData = {"Order ID","Order date","Custumer Name","Country"};
                String[] tempData = new String[4];

                st = new StringTokenizer(data,",");
                String originalData = st.nextToken();

                for(int i=0; i < fieldData.length ; i++) {
                    boolean isUpdate = getYesorNo("Do you want change data " + fieldData[i]);
                    originalData = st.nextToken();
                    if (isUpdate){
                        konsolInput = new Scanner(System.in);
                        System.out.print("\ninput new" + fieldData[i] );
                        tempData[i] = konsolInput.nextLine();
                    } else {
                        tempData[i] = originalData;
                    }
                }

                st = new StringTokenizer(data,",");
                st.nextToken();
                System.out.println("\nUpdated Data :");
                System.out.println("---------------------------------------");
                System.out.println("Order ID       : " + st.nextToken() + " -> " + tempData[0]);
                System.out.println("Order date     : " + st.nextToken() + " -> " + tempData[1]);
                System.out.println("Costumer nam   : " + st.nextToken() + " -> " + tempData[2]);
                System.out.println("Country        : " + st.nextToken() + " -> " + tempData[3]);
                boolean isUpdate = getYesorNo("Are you sure want update these data");

                if (isUpdate){
                    boolean isExist = findOrderInDatabase(tempData,false);

                    if(isExist){
                        System.err.println("We find same data in database, \nPlease delete prevelous data first");
                        bufferedOutput.write(data);
                    } else {
                        String orderID = tempData[0];
                        String orderDate = tempData[1];
                        String custName = tempData[2];
                        String Country = tempData[3];
                        bufferedOutput.write(orderID + "," + orderDate + ","+ custName +"," + Country );
                    }
                } else {
                    bufferedOutput.write(data);
                }
            } else {
                bufferedOutput.write(data);
            }
            bufferedOutput.newLine();

            data = bufferedInput.readLine();
        }
        bufferedOutput.flush();
        database.delete();
        tempDB.renameTo(database);

    }
    private static void deleteDataOrder() throws  IOException{
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        System.out.println("Order list");
        showData();

        Scanner konsolInput = new Scanner(System.in);
        System.out.print("\ninput number data order that will be deleted : ");
        int deleteNum = konsolInput.nextInt();

        boolean isFound = false;
        int entryCounts = 0;

        String data = bufferedInput.readLine();
        while (data != null){
            entryCounts++;
            boolean isDelete = false;

            StringTokenizer st = new StringTokenizer(data,",");
            if (deleteNum == entryCounts){
                System.out.println("\nData that will be deleted :");
                System.out.println("-----------------------------------");
                System.out.println("Order ID       : " + st.nextToken());
                System.out.println("Order date     : " + st.nextToken());
                System.out.println("Costumer nam   : " + st.nextToken());
                System.out.println("Country        : " + st.nextToken());
                isDelete = getYesorNo("Are you sure will delete this order?");
                isFound = true;
            }
            if(isDelete){
                System.out.println("Data delete complited ");
            } else {
                bufferedOutput.write(data);
                bufferedOutput.newLine();
            }
            data = bufferedInput.readLine();
        }

        if(!isFound){
            System.err.println("Order not found");
        }

        bufferedOutput.flush();
        database.delete();
        tempDB.renameTo(database);
    }
    private static void showData() throws IOException{
        FileReader fileInput;
        BufferedReader bufferInput;
        try {
            fileInput = new FileReader("database.txt");
            bufferInput = new BufferedReader(fileInput);
        } catch (Exception e){
            System.err.println("please add your data order first");
            System.err.println("Database not found");
            addData();
            return;
        }

        System.out.println("\n| No |\t Order ID        |\tOrder Date |\tCustomer Name          |\tCountry");
        System.out.println("--------------------------------------------------------------------------------------------");
        String data = bufferInput.readLine();
        int numberData = 0;
        while(data != null) {
            numberData++;
            StringTokenizer stringToken = new StringTokenizer(data, ",");
            System.out.printf("| %2d ", numberData);
            System.out.printf("|\t%4s  ", stringToken.nextToken());
            System.out.printf("|\t%-7s   ", stringToken.nextToken());
            System.out.printf("|\t%-20s   ", stringToken.nextToken());
            System.out.printf("|\t%s   ", stringToken.nextToken());
            System.out.print("\n");
            data = bufferInput.readLine();
        }
        System.out.println("--------------------------------------------------------------------------------------------");
    }

    private static boolean getYesorNo(String message){
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\n"+message+" (y/n)? ");
        String userOption = terminalInput.next();

        while(!userOption.equalsIgnoreCase("y") && !userOption.equalsIgnoreCase("n")) {
            System.err.println("Please input y or n");
            System.out.print("\n"+message+" (y/n)? ");
            userOption = terminalInput.next();
        }
        return userOption.equalsIgnoreCase("y");

    }

    private static void findData() throws IOException{
        try {
            File file = new File("database.txt");
        } catch (Exception e){
            System.err.println("please add your data order first");
            System.err.println("Database not found");
            addData();
            return;
        }
        Scanner konsolInput = new Scanner(System.in);
        System.out.print("Input keyword to find data: ");
        String findString = konsolInput.nextLine();
        String[] keywords = findString.split("\\s+");

        // kita cek keyword di database
        findOrderInDatabase(keywords,true);
    }

    private static void addData() throws IOException{
        FileWriter fileOutput = new FileWriter("database.txt",true);
        BufferedWriter bufferOutput = new BufferedWriter(fileOutput);
        //input from user
        Scanner konsolInput = new Scanner(System.in);
        String orderID, orderDate, custName, Country;

        System.out.print("input Order ID: ");
        orderID = konsolInput.nextLine();
        System.out.print("input order date, format=(MM/DD/YYYY) : ");
        orderDate = konsolInput.nextLine();
        System.out.print("input customer name: ");
        custName = konsolInput.nextLine();
        System.out.print("input country : ");
        Country = konsolInput.nextLine();

        // check order
        String[] keywords = {orderID+","+orderDate+","+custName+","+Country};
        System.out.println(Arrays.toString(keywords));


        boolean isExist = findOrderInDatabase(keywords,false);

        // write in database
        if (!isExist){
            System.out.println("\nData yang akan anda masukan adalah");
            System.out.println("----------------------------------------");
            System.out.println("Order ID  : " + orderID);
            System.out.println("Order Date : " + orderDate);
            System.out.println("Nama      : " + custName);
            System.out.println("Country        : " + Country);

            boolean isAdd = getYesorNo("Apakah akan ingin menambah data tersebut? ");

            if(isAdd){
                bufferOutput.write(orderID + "," + orderDate + ","+ custName +"," + Country );
                bufferOutput.newLine();
                bufferOutput.flush();
            }

        } else {
            System.out.println("Order has been add to database with data ::");
            findOrderInDatabase(keywords,true);
        }

        bufferOutput.close();
    }

    private static boolean findOrderInDatabase(String[] keywords, boolean isDisplay) throws IOException{
        FileReader fileInput = new FileReader("database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        String data = bufferInput.readLine();
        boolean isExist = false;
        int numberData = 0;


        if (isDisplay) {
            System.out.println("\n| No |\t Order ID        |\tOrder Date\t|\tCustomer Name          |\tCountry");
            System.out.println("--------------------------------------------------------------------------------------------");
        }

        while(data != null){
            isExist = true;
            for(String keyword:keywords){
                isExist = isExist && data.toLowerCase().contains(keyword.toLowerCase());
            }
            if(isExist){
                if(isDisplay) {
                    numberData++;
                    StringTokenizer stringToken = new StringTokenizer(data, ",");
                    System.out.printf("| %2d ", numberData);
                    System.out.printf("|\t%4s  ", stringToken.nextToken());
                    System.out.printf("|\t%-7s\t", stringToken.nextToken());
                    System.out.printf("|\t%-20s   ", stringToken.nextToken());
                    System.out.printf("|\t%s   ", stringToken.nextToken());
                    System.out.print("\n");
                } else{
                    break;
                }
            }
            data = bufferInput.readLine();
        }

        if (isDisplay){
            System.out.println("------------------------------------------------------------------------------------------");
        }

        return isExist;
    }
}











