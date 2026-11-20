/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package inventorysystem;

/**
 *
 * @author Teacher
 */
public class AuthService
{
    public boolean authenticate(String username, String password)
    {

        String correctUsername = "admin";
        String correctPassword = "admin123";

        return username.equals(correctUsername) && password.equals(correctPassword);
    }
    
}
