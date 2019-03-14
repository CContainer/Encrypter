package com.encrypt.test;

import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class Encrypter extends JFrame {
	private JTextField textField;
	private JPasswordField passwordField;
	private JTextArea textArea;
	private JRadioButton radioButton;
	private File file;
    public Encrypter(){
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
    	setTitle("File Encrypter");
    	getContentPane().setLayout(new GridLayout(2,1));
    	
    	JPanel panel = new JPanel();
    	getContentPane().add(panel);
    	panel.setLayout(null);
    	
    	textField = new JTextField();
    	textField.setBounds(97, 33, 350, 33);
    	panel.add(textField);
    	textField.setColumns(10);
    	
    	JButton btnNewButton = new JButton("Scan");
    	btnNewButton.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			selectFile();
    		}
    	});
    	btnNewButton.setBounds(478, 38, 93, 23);
    	panel.add(btnNewButton);
    	
    	passwordField = new JPasswordField();
    	passwordField.setBounds(97, 76, 350, 33);
    	panel.add(passwordField);
    	
    	JButton btnNewButton_1 = new JButton("Encrypt");
    	btnNewButton_1.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e){
    			if(0==passwordField.getPassword().length){
    				textArea.append("请输入密码！\n");
    			}else{
    				String psw = new String((passwordField.getPassword())).trim();
    				textField.setText("");
    				passwordField.setText("");
    				encrypt(psw);
    			}
    			
    		}
    	});
    	btnNewButton_1.setBounds(478, 81, 93, 23);
    	panel.add(btnNewButton_1);
    	
    	JTextPane txtpnContactMeBy = new JTextPane();
    	txtpnContactMeBy.setBackground(SystemColor.control);
    	txtpnContactMeBy.setEditable(false);
    	txtpnContactMeBy.setText("Contact me by QQ:1181303867 ");
    	txtpnContactMeBy.setToolTipText("");
    	txtpnContactMeBy.setBounds(129, 114, 318, 30);
    	panel.add(txtpnContactMeBy);
    	
    	JButton btnNewButton_2 = new JButton("Decrypt");
    	btnNewButton_2.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			if(0==passwordField.getPassword().length){
    				textArea.append("请输入密码！\n");
    			}else if(!file.getName().endsWith(".ecy")){
    				textArea.append("文件类型错误，请重新选择！\n");
    			}else{
    				String psw = new String((passwordField.getPassword())).trim();
//    				textField.setText("");
    				passwordField.setText("");
    				decrypt(psw);
    			}
    		}
    	});
    	btnNewButton_2.setBounds(478, 121, 93, 23);
    	panel.add(btnNewButton_2);
    	
    	JTextPane txtpnFile = new JTextPane();
    	txtpnFile.setEditable(false);
    	txtpnFile.setBackground(SystemColor.control);
    	txtpnFile.setText("File\uFF1A");
    	txtpnFile.setBounds(10, 38, 54, 33);
    	panel.add(txtpnFile);
    	
    	JTextPane txtpnPassword = new JTextPane();
    	txtpnPassword.setEditable(false);
    	txtpnPassword.setBackground(SystemColor.control);
    	txtpnPassword.setText("Password\uFF1A");
    	txtpnPassword.setBounds(10, 76, 73, 33);
    	panel.add(txtpnPassword);
    	
    	radioButton = new JRadioButton("\u5220\u9664\u539F\u6587\u4EF6");
    	radioButton.setBounds(10, 118, 121, 26);
    	
    	panel.add(radioButton);
    	
    	JScrollPane scrollPane = new JScrollPane();
    	getContentPane().add(scrollPane);
    	
    	textArea = new JTextArea();
    	scrollPane.setViewportView(textArea);
    	
        setBounds(400, 250, 625, 350);
     }

	
    private void selectFile(){
    	FileDialog fd = new FileDialog(this,"选择待加密或解密的文件");
    	fd.setVisible(true);
    	if(fd.getFile()!=null){
    		file = new File(fd.getDirectory()+fd.getFile());
    		textField.setText(file.getAbsolutePath());
    	}
    	
    	/*
    	 * 	getAbsolutePath():E:\lzy\yuan\Java_code\Test\sign.jpg
			getPath():sign.jpg
			getParent():null
			getName():sign.jpg
    	 */
    }
    
    private void encrypt(String psw){//加密方法
    	
    	//获取文件扩展名及位数。
    	String suffix = getSuffix(file);
    	if(suffix==null){
    		textArea.append("文件类型错误，请重新选择！\n");
    		return;
    	}
    	int suffN = suffix.length();
    	
    	//获取密码的哈希值及位数.
    	int hash = psw.hashCode();//初始哈希值
    	int code = 0;
    	String sign = "+";
    	if(hash>=0){
    		sign = "+";//获取符号
    		code = hash;//获取值
    	}else{
    		sign = "-";
    		code = -hash;
    	}
    	
    	//获取位数1
    	int codeN = String.valueOf(code).length();
    	String codeNum = "";
    	if(codeN<10){
    		codeNum = "0"+codeN;
    	}else{
    		codeNum = String.valueOf(codeN);
    	}
    	
    	//获取位数2
    	String suffNum = "";
    	if(suffN<10){
    		suffNum = "0"+suffN;
    	}else{
    		suffNum = String.valueOf(suffN);
    	}
    	
    	//整合数据。
    	String filedata = sign+codeNum+suffNum+code+suffix;
    	
    	//写入数据。
    	BufferedInputStream bi = null;
    	BufferedOutputStream bo = null;
    	File newFile = null;
    	try {
    		//创建文件
    		
    		newFile = new File(file.getAbsolutePath().replace(suffix, ".ecy"));
    		bi = new BufferedInputStream(new FileInputStream(file));
			bo = new BufferedOutputStream(new FileOutputStream(newFile));
			char[] ch = String.valueOf(code).toCharArray();
			int[] nums = new int[codeN];
			for(int i=0;i<ch.length;i++){
				nums[i] = Integer.parseInt(String.valueOf(ch[i]));
			}
			byte[] buf = new byte[1024];
			int len = 0;
			
			//执行
			bo.write(filedata.getBytes());
			while((len=bi.read(buf))!=-1){
				for(int i = 0,j=0;i<len;i++,j++){
					if(j<nums.length){
						buf[i] = (byte)(buf[i]^nums[j]);
					}else{
						j=0;
					}
				}
				bo.write(buf,0,len);
			}
			
			//释放资源
			bi.close();
			bo.close();
			textArea.append(file.getName()+"加密成功！\n");
			
			//判断单选框
			if(radioButton.isSelected()){
				file.delete();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			textArea.append("加密失败，请重新启动\n");
			if(bi!=null){
				try {
					bi.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(bo!=null){
				try {
					bo.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(newFile.exists()){
				newFile.delete();				
			}
			return;
		}
    }
    
    private void decrypt(String psw){//解密方法
    	//读取文件数据
    	RandomAccessFile raf = null;
    	BufferedOutputStream bo = null;
    	File newFile = null;
    	try {
			raf = new RandomAccessFile(file, "rw");
			byte[] buf = new byte[1];
			int len =0;
			
			//读取符号
			len = raf.read(buf);
			String sign = new String(buf,0,len);
			if(sign.equals("+")){
				sign = "";
			}else{
				sign = "-";
			}
			
			//读取code长度
			buf = new byte[2];
			len = raf.read(buf);
			String codeN = new String(buf,0,len);
			if(codeN.startsWith("0")){
				codeN = String.valueOf(codeN.charAt(1));
			}
			int codeNum = Integer.parseInt(codeN);
			
			//读取长度2
			len = raf.read(buf);
			String suffN = new String(buf,0,len);
			if(suffN.startsWith("0")){
				suffN = String.valueOf(suffN.charAt(1));
			}
			int suffNum = Integer.parseInt(suffN);
			
			//读取code
			buf = new byte[codeNum];
			len = raf.read(buf);
			String code_str = new String(buf,0,len);
			int code = Integer.parseInt(code_str);
			
			//读取类型
			buf = new byte[suffNum];
			len = raf.read(buf);
			String suffix = new String(buf,0,len);
			
			//整合
			int hash = Integer.parseInt(sign+code_str);
			//int length;
			
			//判断
			
			if(psw.hashCode()!=hash){
				textArea.append("密码错误，请重新输入！\n");
				passwordField.setText("");
				raf.close();
				return;
			}
			//解密
			newFile = new File(file.getAbsolutePath().replace(".ecy",suffix));
			bo = new BufferedOutputStream(new FileOutputStream(newFile));
			buf = new byte[1024];
			
			char[] ch = String.valueOf(code).toCharArray();
			int[] nums = new int[codeNum];
			for(int i=0;i<ch.length;i++){
				nums[i] = Integer.parseInt(String.valueOf(ch[i]));
			}
			
			while((len=raf.read(buf))!=-1){
				for(int i = 0,j=0;i<len;i++,j++){
					if(j<nums.length){
						buf[i] = (byte)(buf[i]^nums[j]);
					}else{
						j=0;
					}
				}
				bo.write(buf,0,len);
			}
			
			
			//释放资源
			raf.close();
			bo.close();
			textField.setText("");
			textArea.append(file.getName()+"解密成功！\n");
			
			//判断单选框
			if(radioButton.isSelected()){
				file.delete();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			textArea.append("解密失败，请重新启动\n");
			if(raf!=null){
				try {
					raf.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(bo!=null){
				try {
					bo.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(newFile.exists()){
				newFile.delete();				
			}
			return;
		}
    }
    
    private String getSuffix(File file){//获取文件扩展名方法
    	String name = file.getName();
    	int pos = name.lastIndexOf('.');
    	if(pos==0){
    		return null;
    	}
    	String suffix = name.substring(pos);
    	return suffix;
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Encrypter gui = new Encrypter();
		gui.setVisible(true);

	}
}
