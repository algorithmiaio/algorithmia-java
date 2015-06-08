package algorithmia.algo;

import algorithmia.AlgorithmiaConf;
import java.lang.IllegalArgumentException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A reference to an algorithm and possibly a specific version.
 */
public class AlgorithmRef {
    public final String username;
    public final String algoname;
    public final Version version;

    /**
     * Build an AlgorithmRef from a username, algorithm name pair
     * @param username the owner of the algorithm
     * @param algoname the name of the algorithm
     */
    public AlgorithmRef(String username, String algoname) {
        this(username, algoname, Version.Latest());
    }
    /**
     * Build an AlgorithmRef from a username, algorithm name, and version
     * @param username the owner of the algorithm
     * @param algoname the name of the algorithm
     * @param version the version of the algorithm
     */
    public AlgorithmRef(String username, String algoname, Version version) {
        this.username = username;
        this.algoname = algoname;
        this.version = version;
    }
    /**
     * Build an AlgorithmRef from an algorithm url like "/user/algo/version"
     * @param algo_url a string reference to an algorithm of the form "/user/algo" or "/user/algo/version"
     */
    public AlgorithmRef(String algo_url) {
        // Handle "algo://user/algo" using RegEx
        final Pattern pattern = Pattern.compile("^(algo:/)?/?(?<user>\\w+)/(?<algo>\\w+)(/(?<version>[\\w\\.]+))?$");
        final Matcher matcher = pattern.matcher(algo_url);
        if(matcher.find()) {
            this.username = matcher.group("user");
            this.algoname = matcher.group("algo");
            this.version  = new Version(matcher.group("version"));
        } else {
            throw new IllegalArgumentException("AlgorithmRef url must be of the form \"algo://user/algo(/version)\"");
        }
    }

    /**
     * Resolves this file reference into an HTTP url
     * @return the HTTP url for this file
     */
    public String url() {
        if(version.isLatest()) {
            return AlgorithmiaConf.apiAddress() + "/v1/algo/" + username+ "/" + algoname;
        } else {
            return AlgorithmiaConf.apiAddress() + "/v1/algo/" + username+ "/" + algoname + "/" + version.toString();
        }
    }

    @Override
    public String toString() {
        if(version.isLatest()) {
            return "algo://"+username+"/"+algoname;
        } else {
            return "algo://"+username+"/"+algoname+"/"+version.toString();
        }

    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
