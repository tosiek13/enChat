package model.containers.temporary;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import model.Account;
import model.exceptions.ElementNotFoundException;

import java.util.*;

/**
 * Created by tochur on 01.05.15.
 *
 * This class holds nicks of all logged users and
 * associates them with User objects.
 */
@Singleton
public class Logged {

    /*List of all logged capable to interact*/
    private Map<Integer, Account> logged;
    private Map<Integer, String> loggedNicks;

    @Inject
    public Logged(){
        this.logged = new HashMap<>();
        this.loggedNicks = new HashMap<>();
    }


    /**
     * Adding new unique User to Logged group.
     * @param ID
     * @param account
     */
    void addNew(Integer ID, Account account){
        logged.put(ID, account);
        loggedNicks.put(ID, account.getNick());
    }

    /**
     * Removes user from ActiveUsers
     * @param ID
     * @throws ElementNotFoundException
     */
    void remove (Integer ID) throws ElementNotFoundException {
        if ( logged.remove(ID) == null )
            throw new ElementNotFoundException();
        loggedNicks.remove(ID);
    }

    Map<Integer, String> getIDNickMap(){
        return loggedNicks;
    }

    public String getNick(Integer id){
        return loggedNicks.get(id);
    }

    public Set<Integer> getIDs(){
        return loggedNicks.keySet();
    }

    public Collection<Account> getAccounts(){
        return Collections.unmodifiableCollection(logged.values());
    }

    public Map<Integer, Account> getMap() { return Collections.unmodifiableMap(logged); }

    public Collection<String> getNicks(){ return  Collections.unmodifiableCollection(loggedNicks.values()); }

    /**
     * Returns the id of the user with specified nick. If no user with specified nick is logged return null
     * @param nick - login of the user that id is searched
     * @return id of the user with specified nick. If no user with specified nick is logged return null
     */
    public Integer getUserID(String nick){
        for (Map.Entry<Integer, String> pair : loggedNicks.entrySet()){
            if(pair.getValue().equals(nick))
                return pair.getKey();
        }
        return null;
    }
}
