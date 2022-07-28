package rahadian.com;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BotMain extends TelegramLongPollingBot {

    Statement stm;

    private Object[][] dataTable = null;
    SendMessage message=new SendMessage();
    KoneksiMysql kon = new KoneksiMysql("bot-tele");
    Connection Con = kon.getConnection();


    //Fungsi Chat
    public void kirimPesan(String id, String pesan) {
        SendMessage message = new SendMessage();
        message.setChatId(id);
        message.setText(pesan);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            if (id=="") {
                javax.swing.JOptionPane.showMessageDialog(null, "Pilih ID");
            } if (pesan=="") {
                javax.swing.JOptionPane.showMessageDialog(null, "Pesan Masih Kosong");
            }else {
                javax.swing.JOptionPane.showMessageDialog(null, "Gagal mengirim pesan");
            }
            e.printStackTrace();
        }
    }


    //Fungsi Broadcsast
    public void sendPesanBroadcast(String pesan){
        for (int i = 0; i < (cbID().size()); i++) {
            try {
                message.setChatId(cbID().get(i));
                message.setText(pesan);
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    //get cbID
    public ArrayList<Long> cbID(){
        ArrayList<Long> cbID = new ArrayList<Long>();
        try {
            KoneksiMysql kon = new KoneksiMysql("localhost","root","","bot-tele");
            Connection Con = kon.getConnection();
            Statement stm = Con.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM user");
            while(rs.next()){
                cbID.add(rs.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cbID;
    }

    public void simpan(String [] data) {
        String nama = data[0], id = data[1];
        try{
            System.out.println("masuk insert botClass");
            String query = "INSERT into user VALUES('"+nama+"','"+id+"')";
            stm = Con.createStatement();
            stm.executeUpdate(query);
        } catch(SQLException e){
            System.out.println("Error botClass 1#: " + e);
        }
    }

    //cek member
    public boolean cekMember(long id){
        final boolean[] hasilMember = {false};
        int i = 0;
        ArrayList p = cbID();
        p.forEach(o -> {
            if(o.equals(id)){
                System.out.println("proses: " + o + "///" + id);
                hasilMember[0] = true;
            }
        });
        return hasilMember[0];
    }

    //simpan history pesan ke database history
    public void simpanHistory(String [] data){
        String  nama = data[0], id = data[1], pesan = data[2];
        System.out.println("ini masuk"+nama + " " + id + " " + pesan);
        try{
            System.out.println("masuk insert botClass");
            String query = "INSERT INTO `history`(`nama`, `id`, `pesan`) VALUES ('"+nama+"','"+id+"','"+pesan+"')";
            stm = Con.createStatement();
            stm.executeUpdate(query);
        } catch(SQLException e){
            System.out.println("Error botClass 1#: " + e);
        }
    }





    @Override
    public String getBotUsername() {
        return "rahadian_telebot";
    }

    @Override
    public String getBotToken() {
        return "5309998493:AAF0UOOb_Wxh5oGfMxjjHsxLiSHGa2aXrM4";
    }
    @Override
    public void onUpdateReceived(Update update) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        //______________________________________________Button Menu_________________________________________//
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        ArrayList<String> menu = new ArrayList<>();
        row.add("start");
        row.add("daftar");
        row.add("about");
        row.add("developer");
        for (int i = 0; i < menu.size(); i++) {
            row.add(menu.get(i));
        }
        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        //______________________________________________Button Menu_________________________________________//
        String command;
        command = update.getMessage().getText();
        message.setChatId(update.getMessage().getChatId());
        if (update.hasMessage() && update.getMessage().hasText() && cekMember(update.getMessage().getFrom().getId())) {
            String message_text = update.getMessage().getText();
            String user_name = update.getMessage().getChat().getFirstName();
            formAdmin.taHistory.append(user_name + ": " + message_text + "\n");
            SendMessage message=new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            //______________________________________________Command_________________________________________//
            System.out.println("ini isi nilai command: " + command);
            command = command.toLowerCase();
            switch (command) {
                case "start" -> {
                    String pesan = "BEEBOoo.. 0o0 Halo Nama Saya BotTele_Rahadian-0.1";
                    message.setText(pesan);
                    formAdmin.taHistory.append(getBotUsername() + " : " + pesan + "\n");
                }
                case "about" -> {
                    String pesan = "Saya adalah bot yang dibuat oleh Rahadian Kristiyanto Untuk Memenuhi Tugas Praktikum Akhir Mata Kuliah Pemrograman Berbasi Objek";
                    message.setText(pesan);
                    formAdmin.taHistory.append(getBotUsername()+ " : " + pesan + "\n" );
                }
                case "developer" -> {
                    String pesan = "Saya dibuat oleh : Rahadian Kristiyanto Rachman  NIM : A11.2020.12724";
                    message.setText(pesan);
                    formAdmin.taHistory.append(getBotUsername()+ " : " + pesan + "\n" );
                }
                case "daftar" -> {
                    String pesan = "Anda Sudah Terdaftar";
                    message.setText(pesan);
                    formAdmin.taHistory.append(getBotUsername()+ " : " + pesan + "\n" );
                }
                default -> {
                    System.out.println(update.getMessage() + "Saya tidak mengerti perintah yang anda tulis");
                    message.setText("Command tidak tersedia");
                    formAdmin.taHistory.append(getBotUsername()+ " : " + "Command tidak tersedia" + "\n" );
                }

            }
            try {
                execute(message);
            } catch (TelegramApiException e) {
                System.out.println("Pesan gagal dikirim: " + e);
                System.out.println("Error botClass 2#: " + e);
            }

        } else {
            switch (command) {
                case "daftar" -> {
                    String nama = update.getMessage().getFrom().getFirstName();
                    String id = update.getMessage().getFrom().getId().toString();
                    String [] data = {
                            nama,
                            id
                    };
                    simpan(data);
                    String pesan = "Terimakasih telah mendaftar ";
                    formAdmin.taHistory.append(getBotUsername()+ " : " + pesan + "\n" );
                    message.setText(pesan);
                }
                default -> {
                    String pesan = "daftar dulu";
                    message.setText(pesan);
                    formAdmin.taHistory.append(getBotUsername() + " : " + pesan + "\n");
                }
            }
            try {
                execute(message);
            } catch (TelegramApiException e) {
                System.out.println("Pesan gagal dikirim: " + e);
                System.out.println("Error botClass 2#: " + e);
            }
        }
        //simpan history pesan ke database history
        if (update.hasMessage() && update.getMessage().hasText()) {

            String nama = update.getMessage().getChat().getFirstName();
            String id_pesan = update.getMessage().getChat().getId().toString();
            String isipesan = update.getMessage().getText();
            String [] data = {
                    nama,
                    id_pesan,
                    isipesan
            };
            System.out.println("adalah"+data[0] + " " + data[1] + " " + data[2]);
            simpanHistory(data);
        }
        }
    }

