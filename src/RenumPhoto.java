import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

public class RenumPhoto extends Frame implements ActionListener {
    /**
	 * 
	 */
	//private static final long serialVersionUID = -8716670781092230092L;
	static TextField photo_directory_textfield;
    static TextField renum_photo_textfield;
    private static Choice format_choice;

    public RenumPhoto(String title) {
        // set frame title
        super(title);
        // add detection on widows close events
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        this.setLayout(null);
        this.setLayout(new BorderLayout(10, 10));

        Panel renumPanel = new Panel();
        renumPanel.setLayout(null);
        renumPanel.setBounds(10, 10, 500, 200);

        photo_directory_textfield = new TextField(100);
        //photo_directory_textfield.setEditable(false);
        photo_directory_textfield.setBounds(220, 10, 200, 30);
        renumPanel.add(photo_directory_textfield);
        
        // Create pushbutton photo_directory_button and put it in renumPanel Panel
        Button photo_directory_button = new Button("Choose photos directory");
        photo_directory_button.setBounds(10, 10, 160, 30);
        photo_directory_button.setActionCommand("choose");
        photo_directory_button.addActionListener(this);
        renumPanel.add(photo_directory_button);

        Label renum_photo_label = new Label("New photos names :");
        renum_photo_label.setBounds(10, 50, 160, 30);
        renumPanel.add(renum_photo_label);

        renum_photo_textfield = new TextField(100);
        renum_photo_textfield.setEditable(true);
        renum_photo_textfield.setBounds(220, 50, 150, 30);
        renumPanel.add(renum_photo_textfield);
        
        Label format_label = new Label("Format des fichiers :");
        format_label.setBounds(10, 90, 160, 30);
        renumPanel.add(format_label);

        format_choice = new Choice();
        format_choice.addItem("YYYYMMDD_HHMMSS");
        format_choice.addItem("YYMMDD_HHMMSS");
        format_choice.addItem("Incremental");
        format_choice.setBounds(220, 90, 150, 30);
        renumPanel.add(format_choice);

        // Create pushbutton photo_directory_button and put it in renumPanel Panel
        Button renum_photo_button = new Button("Proceed");
        renum_photo_button.setBounds(50, 130, 70, 50);
        renum_photo_button.setActionCommand("proceed");
        renum_photo_button.addActionListener(this);
        renumPanel.add(renum_photo_button);

        // Create pushbutton photo_directory_button and put it in renumPanel Panel
        Button cancel_button = new Button("Cancel");
        cancel_button.setBounds(200, 130, 70, 50);
        cancel_button.setActionCommand("quit");
        cancel_button.addActionListener(this);
        renumPanel.add(cancel_button);

        this.add(renumPanel);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("quit")) {
            System.exit(0);
        }
        if (command.equals("choose")) {
            FileDialog photo_directory_dialog = new FileDialog(this, "Choose photo directory");
            photo_directory_dialog.setVisible(true);
            photo_directory_textfield.setText(photo_directory_dialog.getDirectory());
            int last_index = photo_directory_dialog.getDirectory().length()-1;
            int first_index = photo_directory_dialog.getDirectory().lastIndexOf('\\', last_index -1)+1;
            System.out.println("first_index=" + first_index + " last_index=" + last_index);
            String prev_name_photo = photo_directory_dialog.getDirectory().substring(first_index, photo_directory_dialog.getDirectory().length()-1);
            prev_name_photo = prev_name_photo.replaceFirst("[^a-zA-Z]*", "");
            renum_photo_textfield.setText(prev_name_photo);
        }
        if (command.equals("proceed")) {
            String directory = photo_directory_textfield.getText();
            String new_name = renum_photo_textfield.getText();
            //String name_format = new String("Incremental");
            String name_format = format_choice.getSelectedItem();
            File f = new File(directory);
            File[] files = f.listFiles(new MyFilter());
            Vector<File> modified_files_list = new Vector<File>();
            //for (int i = 0; i < files.length; i++) {
            //    String modified_time = String.valueOf(files[i].lastModified());
            //    System.out.println("File " + files[i].getName() + " modified at " + modified_time);
            //}
            //System.out.print("\n");
            if (name_format.equals("Incremental"))
            {
	            for (int i = 0; i < files.length; i++) {
	                String modified_time = String.valueOf(files[i].lastModified());
	                int j = 0;
	                boolean inserted = false;
	                while(j < modified_files_list.size()) {
	                    File file_from_list = modified_files_list.elementAt(j);
	                    String file_modified_time = String.valueOf(file_from_list.lastModified());
	                    if (modified_time.compareTo(file_modified_time) < 0) {
	                        modified_files_list.insertElementAt(files[i], j);
	                        j = modified_files_list.size();
	                        inserted = true;
	                    }
	                    j++;
	                }
	                if (!inserted) {
	                    modified_files_list.addElement(files[i]);
	                }
	            }
	            //for(int i = 0; i < modified_files_list.size(); i++) {
	            //    File file_from_list = (File)(modified_files_list.elementAt(i));
	            //    String file_name = file_from_list.getName();
	            //    System.out.println("File " + i + " : " + file_name);
	            //}
	            //System.out.print("\n");
	            int file_num = 0;
	            for(int i = 0; i < modified_files_list.size(); i++) {
	                File file_from_list = modified_files_list.elementAt(i);
	                String file_number = String.valueOf(file_num+1);
	                if (file_number.length() == 1)
	                    file_number = "00" + file_number;
	                else if (file_number.length() == 2)
	                    file_number = "0" + file_number;
	                String new_file_name = new_name + " " + file_number + ".JPG";
	                //System.out.println(file_from_list.getParent());
	                File new_file = new File(file_from_list.getParent() + "\\" + new_file_name);
	                file_from_list.renameTo(new_file);
	                //System.out.println(new_file.getPath());
	                file_num++;
	            }
            }
            if (name_format.equals("YYMMDD_HHMMSS"))
            {
            	int file_num = 0;
            	for (int i = 0; i < files.length; i++) {
            		String file_number = String.valueOf(file_num+1);
	                if (file_number.length() == 1)
	                    file_number = "00" + file_number;
	                else if (file_number.length() == 2)
	                    file_number = "0" + file_number;
	                String new_file_name = new_name + "." + file_number + ".JPG";
            		String creation_date = getCreateDateMetaData(files[i]);
            		if (creation_date != null) {
            			creation_date = creation_date.substring(3, creation_date.length());
            			creation_date = creation_date.substring(0, creation_date.length()-1);
            			creation_date = creation_date.replace(' ', '_');
            			creation_date = creation_date.replaceAll(":", "");
            			new_file_name = creation_date + "." + new_name + ".JPG";
            		}
            		File new_file = new File(files[i].getParent() + "\\" + new_file_name);
            		files[i].renameTo(new_file);
            		//System.out.println("rename " + files[i].getPath() + 
            		//		" to " + files[i].getParent() + "\\" + new_file_name);
	                file_num++;
            	}
            }
            if (name_format.equals("YYYYMMDD_HHMMSS"))
            {
            	int file_num = 0;
            	for (int i = 0; i < files.length; i++) {
            		String file_number = String.valueOf(file_num+1);
	                if (file_number.length() == 1)
	                    file_number = "00" + file_number;
	                else if (file_number.length() == 2)
	                    file_number = "0" + file_number;
	                String new_file_name = new_name + "." + file_number + ".JPG";
            		String creation_date = getCreateDateMetaData(files[i]);
            		if (creation_date != null) {
            			creation_date = creation_date.substring(1, creation_date.length());
            			creation_date = creation_date.substring(0, creation_date.length()-1);
            			creation_date = creation_date.replace(' ', '_');
            			creation_date = creation_date.replaceAll(":", "");
            			new_file_name = creation_date + "." + new_name + ".JPG";
            		}
            		File new_file = new File(files[i].getParent() + "\\" + new_file_name);
            		files[i].renameTo(new_file);
            		//System.out.println("rename " + files[i].getPath() + 
            		//		" to " + files[i].getParent() + "\\" + new_file_name);
	                file_num++;
            	}
            }
            System.exit(0);
        }
    }

    private String getCreateDateMetaData(File file) {
    	IImageMetadata metadata = null;
        try {
            metadata = Sanselan.getMetadata(file);
        } catch (ImageReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            TiffField field = jpegMetadata.findEXIFValue(TiffConstants.EXIF_TAG_CREATE_DATE);
            if (field != null)
                return field.getValueDescription();
            else
            	return null;
        }
        return null;
    }
    /**
     * Read metadata from image file and display it. 
     * @param file
     */
    public void readMetaData(File file) {
        IImageMetadata metadata = null;
        try {
            metadata = Sanselan.getMetadata(file);
        } catch (ImageReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            System.out.println("\nFile: " + file.getPath());
            printTagValue(jpegMetadata,
                TiffConstants.TIFF_TAG_XRESOLUTION);
            printTagValue(jpegMetadata,
                TiffConstants.TIFF_TAG_DATE_TIME);
            printTagValue(jpegMetadata,
                TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
            printTagValue(jpegMetadata,
                TiffConstants.EXIF_TAG_CREATE_DATE);
            printTagValue(jpegMetadata,
                TiffConstants.EXIF_TAG_ISO);
            printTagValue(jpegMetadata,
                TiffConstants.EXIF_TAG_SHUTTER_SPEED_VALUE);
            printTagValue(jpegMetadata,
                TiffConstants.EXIF_TAG_APERTURE_VALUE);
            printTagValue(jpegMetadata,
                TiffConstants.EXIF_TAG_BRIGHTNESS_VALUE);
            
            System.out.println("EXIF items -");
            ArrayList items = jpegMetadata.getItems();
            for (int i = 0; i < items.size(); i++) {
                Object item = items.get(i);
                System.out.println("    " + "item: " +
                    item);
            }
            System.out.println();
        }
    }

    private static void printTagValue(
            JpegImageMetadata jpegMetadata, TagInfo tagInfo) {
            TiffField field = jpegMetadata.findEXIFValue(tagInfo);
            if (field == null) {
                System.out.println(tagInfo.name + ": " +
                    "Not Found.");
            } else {
                System.out.println(tagInfo.name + ": " +
                    field.getValueDescription());
            }
        }

            

    public RenumPhoto() {}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Frame f = new RenumPhoto("renumPhoto");
        f.pack();
        //f.setSize(800,600);
		f.setResizable(false);
        f.setVisible(true);
	}

}
class MyFilter implements FileFilter {
    public boolean accept(File file){
        boolean result = true;
        String file_name = file.getName();
        if (file_name == null)
            result = true;
        else {
            if ((file_name.endsWith(".JPG"))||(file_name.endsWith(".jpg"))||(file_name.endsWith(".JPEG"))||(file_name.endsWith(".jpeg")))
                result = true;
            else
                result = false;
        }
	  
        return result;
    }

    public String getDescription() {
        return "*.JPG";
    }
}
