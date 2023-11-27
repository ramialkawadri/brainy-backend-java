package com.brainy.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.brainy.model.entity.SharedFile;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateSharedFileAccessRequest;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
public class DefaultFileShareDao implements FileShareDao {

    private EntityManager entityManager;
    private UserDao userDao;

    public DefaultFileShareDao(EntityManager entityManager, UserDao userDao) {
        this.entityManager = entityManager;
        this.userDao = userDao;
    }

    @Override
    public List<SharedFile> getFilesSharedWithUser(User user) {
        String query = "FROM SharedFile s WHERE s.sharedWith=:user";

        return entityManager
                .createQuery(query, SharedFile.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Override
    public List<SharedFile> getFileShares(User user, String filename) {
        String query = "FROM SharedFile s WHERE s.fileOwner=:user AND s.filename=:filename";

        return entityManager
                .createQuery(query, SharedFile.class)
                .setParameter("user", user)
                .setParameter("filename", filename)
                .getResultList();
    }

    @Override
    public boolean isFileSharedWith(
            String fileOwnerUsername,
            String filename,
            String sharedWithUsername) {

        return getSharedFile(fileOwnerUsername, filename, sharedWithUsername) != null;
    }

    @Override
    @Transactional
    public void shareFile(
            String fileOwnerUsername,
            String filename,
            String sharedWithUsername,
            boolean canEdit) throws BadRequestException {

        User fileOwner = userDao.findUserByUserName(fileOwnerUsername);
        User sharedWith = userDao.findUserByUserName(sharedWithUsername);

        if (sharedWith == null)
            throw new BadRequestException("cannot find a user with username " + sharedWithUsername);

        SharedFile sharedFile = new SharedFile(fileOwner, filename, sharedWith, canEdit);

        entityManager.persist(sharedFile);
    }

    @Override
    @Transactional
    public void deleteFileShare(
            String fileOwnerUsername,
            String filename,
            String sharedWithUsername) {

        SharedFile sharedFile = getSharedFile(
                fileOwnerUsername, filename, sharedWithUsername);

        entityManager.remove(sharedFile);
    }

    @Override
    @Transactional
    public void updateSharedFileAccess(
            String fileOwnerUsername,
            String filename,
            String sharedWithUsername,
            UpdateSharedFileAccessRequest request) {

        SharedFile sharedFile = getSharedFile(fileOwnerUsername, filename,
                sharedWithUsername);

        sharedFile.setCanEdit(request.canEdit());

        entityManager.merge(sharedFile);
    }

    private SharedFile getSharedFile(
            String fileOwnerUsername,
            String filename,
            String sharedWithUsername) {

        String query = "FROM SharedFile s WHERE s.filename=:filename AND s.fileOwner.username=:fileOwner AND s.sharedWith.username=:sharedWith";

        try {
            return entityManager.createQuery(query, SharedFile.class)
                    .setParameter("filename", filename)
                    .setParameter("fileOwner", fileOwnerUsername)
                    .setParameter("sharedWith", sharedWithUsername)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
