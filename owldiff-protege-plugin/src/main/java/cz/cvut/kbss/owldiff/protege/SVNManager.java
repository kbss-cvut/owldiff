/*
 * Copyright (c) 2012 Czech Technical University in Prague.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package cz.cvut.kbss.owldiff.protege;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNManager {

    private static final Logger LOG = Logger.getLogger(SVNManager.class
            .getName());

    private static SVNManager INSTANCE;
    private static SVNClientManager clientManager;
    private File managedFile = null;

    private SVNManager() {
    }

    public static synchronized SVNManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SVNManager();
        }
        return INSTANCE;
    }

    public boolean connect(final String login, final String pass,
                           final File file) {
        try {
            final ISVNAuthenticationManager m = SVNWCUtil
                    .createDefaultAuthenticationManager(login, pass);
            clientManager = SVNClientManager.newInstance(SVNWCUtil
                    .createDefaultOptions(true), m);

            SVNRepositoryFactoryImpl.setup();
            DAVRepositoryFactory.setup();
            FSRepositoryFactory.setup();

            final SVNRepository r = SVNRepositoryFactory.create(SVNURL
                    .parseURIEncoded(clientManager.getWCClient().doInfo(file,
                            SVNRevision.BASE).getURL().toString()));
            r.setAuthenticationManager(m);
            r.testConnection();
            managedFile = file;
            return true;
        } catch (SVNException e) {
            LOG.log(Level.SEVERE, "An error occured during login.", e);
            return false;
        }
    }

    public List<Long> getRevisionNumbers() {
        final List<Long> fileRevisions = new ArrayList<Long>();
        try {
            clientManager.getLogClient().doLog(new File[]{managedFile},
                    SVNRevision.UNDEFINED, /* peg revision */
                    SVNRevision.UNDEFINED, /* start, defaults to peg */
                    SVNRevision.UNDEFINED, /* end, defaults to 0 */
                    false, /* stop on copy */
                    false, /* include paths */
                    false, /* include mergeinfo */
                    -1, /* no limit */
                    null, /* all revprops */
                    new ISVNLogEntryHandler() {
                        // [9409, 6485, 6484, 5804]
                        public void handleLogEntry(SVNLogEntry logEntry)
                                throws SVNException {
                            fileRevisions.add(logEntry.getRevision());
                        }
                    });
        } catch (SVNException e) {
            LOG.severe("An error occured during fetching revision numbers.");
        }
        return fileRevisions;
    }

    public boolean isUnderSVN() {
        try {
            return clientManager.getStatusClient().doStatus(managedFile, false)
                    .getContentsStatus().equals(
                            SVNStatusType.STATUS_UNVERSIONED);
        } catch (SVNException e) {
            LOG.log(Level.SEVERE, "An error occured while getting status for '"
                    + managedFile + "'", e);
            return false;
        }
    }

    public File getFileForRevision(long revision) {
        return getFile(SVNRevision.create(revision));
    }

    public File getBaseFile() {
        return getFile(SVNRevision.BASE);
    }

    public File getHeadFile() {
        return getFile(SVNRevision.HEAD);
    }

    private File getFile(SVNRevision revision) {
        File tmp;
        try {
            tmp = File.createTempFile("tmpOntologyFile", "OWLDiffPlugin");
            tmp.deleteOnExit();
            clientManager.getWCClient().doGetFileContents(managedFile,
                    SVNRevision.UNDEFINED, revision, false,
                    new FileOutputStream(tmp));
            return tmp;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occured during fetching file "
                    + managedFile + " in revision " + revision, e);
            return null;
        } catch (SVNException e) {
            LOG.log(Level.SEVERE, "An error occured during fetching file "
                    + managedFile + " in revision " + revision, e);
            return null;
        }
    }
}
