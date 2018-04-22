package ru.kpfu.itis.water.services.impl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.water.form.AppointmentAddForm;
import ru.kpfu.itis.water.model.*;
import ru.kpfu.itis.water.repositories.AppointmentRepository;
import ru.kpfu.itis.water.repositories.DepartmentRepository;
import ru.kpfu.itis.water.services.AppointmentService;
import ru.kpfu.itis.water.util.AppointmentDocsGenerator;
import ru.kpfu.itis.water.util.AuthenticationUtil;

import java.time.LocalDateTime;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentRepository appointmentRepository;
    private DepartmentRepository departmentRepository;
    private AuthenticationUtil authenticationUtil;
    private AppointmentDocsGenerator appointmentDocsGenerator;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, DepartmentRepository departmentRepository, AuthenticationUtil authenticationUtil, AppointmentDocsGenerator appointmentDocsGenerator) {
        this.appointmentRepository = appointmentRepository;
        this.departmentRepository = departmentRepository;
        this.authenticationUtil = authenticationUtil;
        this.appointmentDocsGenerator = appointmentDocsGenerator;
    }

    @Override
    public Appointment registerUserToAppointment(AppointmentAddForm form, Authentication authentication) {
        User user = authenticationUtil.getUserDataByAuthentication(authentication).getUser();
        Department department = departmentRepository.findById(form.getDepId()).orElseThrow(
                () -> new IllegalArgumentException("Department with id " + form.getDepId() + " does not exist")
        );
        Appointment appointment = Appointment.builder()
                .code(generateAppointmentCode(department, user))
                .user(user)
                .description(form.getDescription())
                .department(department)
                .dateTime(calculateAppointmentTime())
                .build();
        AppointmentDoc appointmentDoc = generateDoc(appointment);
        appointment.setDoc(appointmentDoc);
        appointmentRepository.save(appointment);
        return appointment;
    }

    private String generateAppointmentCode(Department department, User user) {
        return department.getName().substring(0, 1).toUpperCase() + "-" +
                user.getName().substring(0, 1).toUpperCase() +
                user.getSurname().substring(0, 1).toUpperCase();
    }

    private LocalDateTime calculateAppointmentTime() {
        // TODO: 22.04.18
        return LocalDateTime.now();
    }

    private AppointmentDoc generateDoc(Appointment appointment) {
        FileInfo docFileInfo = appointmentDocsGenerator.generateDocByAppointment(appointment);
        return AppointmentDoc.builder().fileInfo(docFileInfo).build();
    }
}
