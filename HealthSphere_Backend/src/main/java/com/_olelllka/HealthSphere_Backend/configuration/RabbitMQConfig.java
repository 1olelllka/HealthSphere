package com._olelllka.HealthSphere_Backend.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    private final String queueCreateUpdateName = "doctors_index_queue";
    private final String queueDeleteName = "doctor_index_delete_queue";
    private final String queueMedicalRecordCreateUpdate = "medical_record_create_update";
    private final String queueMedicalRecordDelete = "medical_record_delete";
    private final String exchangeName = "doctor_exchange";
    private final String medicalExchange = "record_exchange";

    @Bean
    public Queue doctorIndexCreateUpdateQueue() {
        return new Queue(queueCreateUpdateName, true);
    }

    @Bean
    public Queue doctorIndexDeleteQueue() {
        return new Queue(queueDeleteName, true);
    }

    @Bean
    public Queue medicalRecordIndexDeleteQueue() {
        return new Queue(queueMedicalRecordDelete, true);
    }

    @Bean
    public Queue medicalRecordIndexCreateUpdateQueue() {
        return new Queue(queueMedicalRecordCreateUpdate, true);
    }

    @Bean
    public DirectExchange medicalRecordExchange() {
        return new DirectExchange(medicalExchange);
    }

    @Bean
    public DirectExchange doctorDirectExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding doctorIndexCreateUpdateBinding(Queue doctorIndexCreateUpdateQueue, DirectExchange doctorDirectExchange) {
        return BindingBuilder.bind(doctorIndexCreateUpdateQueue).to(doctorDirectExchange).with(queueCreateUpdateName);
    }

    @Bean
    public Binding doctorIndexDeleteBinding(Queue doctorIndexDeleteQueue, DirectExchange doctorDirectExchange) {
        return BindingBuilder.bind(doctorIndexDeleteQueue).to(doctorDirectExchange).with(queueDeleteName);
    }

    @Bean
    public Binding medicalRecordCreateUpdateBinding(Queue medicalRecordIndexCreateUpdateQueue, DirectExchange medicalRecordExchange) {
        return BindingBuilder.bind(medicalRecordIndexCreateUpdateQueue).to(medicalRecordExchange).with(queueMedicalRecordCreateUpdate);
    }

    @Bean
    public Binding medicalRecordDeleteBinding(Queue medicalRecordIndexDeleteQueue, DirectExchange medicalRecordExchange) {
        return BindingBuilder.bind(medicalRecordIndexDeleteQueue).to(medicalRecordExchange).with(queueMedicalRecordDelete);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
