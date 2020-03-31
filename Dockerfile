FROM buoyantio/linkerd:1.7.0

RUN mkdir -p $L5D_HOME/plugins
COPY plugins/*.jar $L5D_HOME/plugins/
