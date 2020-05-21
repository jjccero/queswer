package com.gzu.queswer.controller;

import com.gzu.queswer.common.UserContext;
import com.gzu.queswer.common.UserException;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackupContoller {
    @Autowired
    UserContext userContext;
    @Autowired
    BackupService backupService;

    @GetMapping("/restore")
    public boolean restore() throws UserException {
        userContext.check(UserLogin.SUPER_ADMIN, true);
        return backupService.restore();
    }

    @GetMapping("/backup")
    public boolean backup() throws UserException {
        userContext.check(UserLogin.SUPER_ADMIN, true);
        return backupService.backup();
    }
}
