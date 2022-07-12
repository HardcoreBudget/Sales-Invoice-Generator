package controller;

import model.FileOperations;

public class Main {
    public static void main(String[] s){
        FileOperations fileOperations = new FileOperations("SIG");
        fileOperations.setVisible(true);
    }
}
