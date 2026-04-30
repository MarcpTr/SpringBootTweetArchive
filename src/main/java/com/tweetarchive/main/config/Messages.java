package com.tweetarchive.main.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Configuración de internacionalización (i18n).
 *
 * <p>
 * Define un bean de tipo {@link MessageSource} que permite
 * resolver mensajes desde archivos de propiedades ubicados en el classpath.
 *
 * <p>
 * Los archivos deben seguir el patrón:
 * messages.properties, messages_es.properties, messages_en.properties, etc.
 *
 * <p>
 * Se utiliza {@link ReloadableResourceBundleMessageSource} para permitir
 * la recarga de mensajes sin reiniciar la aplicación.
 */
@Configuration
public class Messages {
    /**
     * Bean que gestiona la resolución de mensajes internacionalizados.
     *
     * @return instancia de {@link MessageSource} configurada con:
     *         <ul>
     *         <li>Base name: "classpath:messages"</li>
     *         <li>Codificación: UTF-8</li>
     *         </ul>
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Ruta base de los archivos de mensajes
        messageSource.setBasename("classpath:messages");
        // Codificación para evitar problemas con caracteres especiales
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
