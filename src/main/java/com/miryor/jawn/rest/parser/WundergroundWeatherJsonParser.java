/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miryor.jawn.rest.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.miryor.jawn.rest.api.HourlyForecast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

/**
 * Created by royrim on 11/22/16.
 */

public class WundergroundWeatherJsonParser implements WeatherJsonParser {
    private final Logger logger = LoggerFactory.getLogger(WundergroundWeatherJsonParser.class);
    
    private InputStream inputStream;
    public WundergroundWeatherJsonParser(String s) {
        try {
            this.inputStream = new ByteArrayInputStream(s.getBytes("UTF-8"));
        }
        catch ( UnsupportedEncodingException e ) {
            // i guess its possible?
            logger.error( "This shouldn't happen", e );
        }
    }
    public WundergroundWeatherJsonParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    public List<HourlyForecast> parseHourlyForecast() throws IOException {
        List<HourlyForecast> list = new ArrayList<HourlyForecast>();
        JsonFactory f = new JsonFactory();
        JsonParser parser = null;
        try {
            parser = f.createParser(new InputStreamReader(inputStream,"UTF-8"));

            JsonToken t;
            while ( ( t = parser.nextToken() ) != null ) {
                if ( t == JsonToken.FIELD_NAME ) {
                    String name = parser.getCurrentName();
                    if ( logger.isDebugEnabled() ) logger.debug( "found " + name );
                    if (name.equals("hourly_forecast")) {
                        t = parser.nextToken();
                        if ( t == JsonToken.START_ARRAY ) {
                            while ( parser.nextToken() == JsonToken.START_OBJECT ) {
                                list.add(parseHourlyForecast(parser));
                            }
                        }
                        else {
                            throw new IOException( "Was expecting START_ARRAY got " + t );
                        }
                    }
                }
            }
            if ( logger.isDebugEnabled() ) logger.debug( "done" );
        }
        finally {
            if ( parser != null ) parser.close();
        }

        return list;
    }

    private HourlyForecast parseHourlyForecast(JsonParser parser) throws IOException {
        HourlyForecast.HourlyForecastBuilder builder = new HourlyForecast.HourlyForecastBuilder();
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t == JsonToken.FIELD_NAME ) {
                String name = parser.getCurrentName();
                if ( name.equals("FCTTIME") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseFctTime(parser,builder);
                }
                else if ( name.equals("temp") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseTemp(parser,builder);
                }
                else if ( name.equals("dewpoint") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseDewpoint(parser,builder);
                }
                else if ( name.equals("sky") ) {
                    t = parser.nextToken();
                    builder.setSky(parser.getValueAsInt());
                }
                else if ( name.equals("wspd") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseWindSpeed(parser,builder);
                }
                else if ( name.equals("wdir") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseWindDir(parser,builder);
                }
                else if ( name.equals("windchill") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseWindChill(parser,builder);
                }
                else if ( name.equals("heatindex") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseHeatIndex(parser,builder);
                }
                else if ( name.equals("qpf") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseQpf(parser,builder);
                }
                else if ( name.equals("snow") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseSnow(parser,builder);
                }
                else if ( name.equals("mslp") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseMslp(parser,builder);
                }
                else if ( name.equals("wx") ) {
                    t = parser.nextToken();
                    builder.setWx(parser.getText());
                }
                else if ( name.equals("uvi") ) {
                    t = parser.nextToken();
                    builder.setUvi(parser.getValueAsInt());
                }
                else if ( name.equals("pop") ) {
                    t = parser.nextToken();
                    builder.setPop(parser.getValueAsInt());
                }
                else if ( name.equals("condition") ) {
                    t = parser.nextToken();
                    builder.setCondition(parser.getText());
                }
                else if ( name.equals("humidity") ) {
                    t = parser.nextToken();
                    builder.setHumidity(parser.getValueAsInt());
                }
                else if ( name.equals("feelslike") ) {
                    if ( parser.nextToken() == JsonToken.START_OBJECT )
                        parseFeelsLike(parser,builder);
                }
                else {
                    if ( logger.isDebugEnabled() ) logger.debug( "Skipping " + name );
                    t = parser.nextToken();
                    if ( t == JsonToken.START_OBJECT ) {
                        parser.skipChildren();
                    }
                }
            }
        }
        return builder.build();
    }

    private void parseFctTime(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("hour") ) {
                builder.setHour( parser.getValueAsInt() );
            }
            else if ( name.equals("hour_padded") ) {
                builder.setHourPadded( parser.getText() );
            }
            else if ( name.equals("min") ) {
                builder.setMinPadded( parser.getText() );
            }
            else if ( name.equals("min_unpadded") ) {
                builder.setMin( parser.getValueAsInt() );
            }
            else if ( name.equals("year") ) {
                builder.setYear( parser.getValueAsInt() );
            }
            else if ( name.equals("mon") ) {
                builder.setMonth( parser.getValueAsInt() );
            }
            else if ( name.equals("mon_padded") ) {
                builder.setMonthPadded( parser.getText() );
            }
            else if ( name.equals("mday") ) {
                builder.setDay( parser.getValueAsInt() );
            }
            else if ( name.equals("mday_padded") ) {
                builder.setDayPadded( parser.getText() );
            }
            else if ( name.equals("epoch") ) {
                builder.setEpoch( parser.getValueAsLong() );
            }
        }
    }

    private void parseTemp(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("english") ) {
                builder.setTempF(parser.getValueAsInt());
            }
            else if ( name.equals("metric" ) ) {
                builder.setTempM(parser.getValueAsInt());
            }
        }
    }

    private void parseDewpoint(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("english") ) {
                builder.setDewPointEnglish(parser.getValueAsInt());
            }
            else if ( name.equals("metric" ) ) {
                builder.setDewPointMetric(parser.getValueAsInt());
            }
        }
    }

    private void parseWindSpeed(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("english") ) {
                builder.setWindSpeedEnglish(parser.getValueAsInt());
            }
            else if ( name.equals("metric" ) ) {
                builder.setWindSpeedMetric(parser.getValueAsInt());
            }
        }
    }

    private void parseWindChill(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("english") ) {
                builder.setWindChillEnglish(parser.getValueAsInt());
            }
            else if ( name.equals("metric" ) ) {
                builder.setWindChillMetric(parser.getValueAsInt());
            }
        }
    }

    private void parseHeatIndex(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("english") ) {
                builder.setHeatIndexEnglish(parser.getValueAsInt());
            }
            else if ( name.equals("metric" ) ) {
                builder.setHeatIndexMetric(parser.getValueAsInt());
            }
        }
    }

    private void parseQpf(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("english") ) {
                builder.setQpfEnglish(parser.getValueAsDouble());
            }
            else if ( name.equals("metric" ) ) {
                builder.setQpfMetric(parser.getValueAsInt());
            }
        }
    }

    private void parseSnow(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("english") ) {
                builder.setSnowEnglish(parser.getValueAsDouble());
            }
            else if ( name.equals("metric" ) ) {
                builder.setSnowMetric(parser.getValueAsInt());
            }
        }
    }

    private void parseMslp(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("english") ) {
                builder.setMslpEnglish(parser.getValueAsDouble());
            }
            else if ( name.equals("metric" ) ) {
                builder.setMslpMetric(parser.getValueAsInt());
            }
        }
    }

    private void parseWindDir(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("dir") ) {
                builder.setWindDirection(parser.getText());
            }
            else if ( name.equals("degrees" ) ) {
                builder.setWindDegrees(parser.getValueAsInt());
            }
        }
    }

    private void parseFeelsLike(JsonParser parser, HourlyForecast.HourlyForecastBuilder builder) throws IOException {
        JsonToken t;
        while( ( t = parser.nextToken() ) != JsonToken.END_OBJECT ) {
            if ( t != JsonToken.FIELD_NAME ) continue;
            String name = parser.getCurrentName();
            parser.nextToken();
            if ( name.equals("english") ) {
                builder.setFeelsLikeF(Integer.parseInt(parser.getText()));
            }
            else if ( name.equals("metric" ) ) {
                builder.setFeelsLikeM(Integer.parseInt(parser.getText()));
            }
        }

    }

}
