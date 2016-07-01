/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsymbolic2.processing;

import java.io.File;
import java.io.FileFilter;

/**
 * A file filter to check if either the midi or mei extensions are given
 * when validating input files by their extensions.
 * 
 * @author Tristano Tenaglia
 */
public class MusicFileFilter implements FileFilter 
{
    private final String[] goodFileExtensions = new String[]{"mei","midi","mid"};
    
    @Override
    public boolean accept(File pathname) 
    {
        for (String extension : goodFileExtensions) 
        {
            if(pathname.getName().toLowerCase().endsWith(extension)) 
            {
                return true;
            }
        }
        return false;
    }
    
}
