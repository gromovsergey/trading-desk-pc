const path =               require('path');
const fs =                 require('fs');
const crypto =             require('crypto');
const webpack =            require('webpack');
const CopyPlugin =         require('copy-webpack-plugin');
const ExtractTextPlugin =  require('extract-text-webpack-plugin');
const HtmlPlugin =         require('html-webpack-plugin');
const UglifyJSPlugin =     require('uglifyjs-webpack-plugin');
const LessCleanCSSPlugin = require('less-plugin-clean-css');
const AotPlugin =          require('@ngtools/webpack').AotPlugin;

const hash = crypto.randomBytes(6).toString('hex');
const outputPath = `bundles/${hash}`;   // html-webpack-plugin does not support path template's [hash]
const banner = `build time: ${new Date().toLocaleString('ru')}\nhash: ${hash}`;
const useProdMode = ['1', 'true'].includes(process.env._USE_PROD_MODE_);
const lang = process.env._LANG_ || 'ru-RU';

/webpack\-dev\-server$/.test(process.argv[1]) || fs.writeFileSync('.base', outputPath);

module.exports = () => {
  let config = {
    context: __dirname,
    entry: {
      head: [
        './node_modules/bootstrap/dist/css/bootstrap.min.css',
        './node_modules/bootstrap/dist/css/bootstrap-theme.min.css',
        './node_modules/font-awesome/css/font-awesome.css',
        './node_modules/eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.css',
        './node_modules/bootstrap-colorpicker/dist/css/bootstrap-colorpicker.css',
        './node_modules/webpack-bootstrap-treeview/dist/bootstrap-treeview.min.css',
        './jqplot/jquery.jqplot.css',
        './css/styles.css',
        './css/extensions/tables.less',
        './css/extensions/grid.less',
        './css/extensions/cinemagoers.less',
        './node_modules/jquery/dist/jquery.min.js',
        './jqplot/jquery.jqplot.min.js',
        './jqplot/plugins/jqplot.dateAxisRenderer.js',
        './jqplot/plugins/jqplot.cursor.js',
        './jqplot/plugins/jqplot.highlighter.js',
        './jqplot/plugins/jqplot.canvasTextRenderer.js',
        './jqplot/plugins/jqplot.canvasAxisLabelRenderer.js',
        './jqplot/plugins/jqplot.canvasAxisTickRenderer.js',
        './jqplot/plugins/jqplot.enhancedLegendRenderer.js',
        './jqplot/plugins/jqplot.barRenderer.js',
        './jqplot/plugins/jqplot.donutRenderer.js',
        './jqplot/plugins/jqplot.categoryAxisRenderer.js',
        './jqplot/plugins/jqplot.pointLabels.js',
        './jqplot/plugins/jqplot.canvasOverlay.js',
        './node_modules/moment/min/moment.min.js',
        './node_modules/eonasdan-bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js',
        './node_modules/bootstrap-colorpicker/dist/js/bootstrap-colorpicker.min.js',
        './node_modules/webpack-bootstrap-treeview/dist/bootstrap-treeview.min.js',
        './node_modules/core-js/client/shim.min.js',
        './node_modules/zone.js/dist/zone.min.js'
      ]
    },

    output: {
      path: path.resolve(__dirname, outputPath),
      publicPath: '',
      filename: `[name]_${hash}.js`,
      chunkFilename: `chunk[id]_${hash}.js`
    },

    resolve: {
      extensions: ['.ts', '.js']
    },

    resolveLoader: {
      modules: [
        'node_modules',
        path.resolve(__dirname, 'loaders')
      ]
    },

    devtool: 'source-map',
    devServer: {
      host: '0.0.0.0',
      port: 3000,
      historyApiFallback: true
    },

    module: {
      exprContextCritical: false,   // suppress compile warnings
      rules: [
        {
          test: /\.min\.js$/,
          loader: 'script-loader'
        },
        {
          test: /\.css$/,
          loader: ExtractTextPlugin.extract({
            use: [
              {
                loader: 'css-loader'
              },
              {
                loader: 'clean-css-loader',
                options: {
                  level: useProdMode ? {1: {specialComments: 0}} : 0
                }
              }
            ]
          })
        },
        {
          test: /\.less$/,
          loader: ExtractTextPlugin.extract({
            use: [
              {
                loader: 'css-loader'
              },
              {
                loader: 'less-loader',
                options: {
                  plugins: [
                    new LessCleanCSSPlugin({
                      advanced: true,
                      keepSpecialComments: 0,
                      sourceMap: true
                    })
                  ].filter(() => useProdMode)
                }
              }
            ]
          })
        },
        {
          test: /\.(eot|ttf|woff2?|svg)(\?[^\/]+)?$/,
          use: [{
            loader: 'url-loader',
            options: {
              limit: 8192,
              name: 'fonts/[name]_[md5:hash:hex:6].[ext]'
            }
          }]
        },
        {
          test: /\.png$/,
          use: [{
            loader: 'url-loader',
            options: {
              limit: 8192,
              name: 'img/[name]_[md5:hash:hex:6].[ext]'
            }
          }]
        }
      ]
    },

    plugins: [
      new webpack.EnvironmentPlugin({
        _USE_PROD_MODE_: false,     // default is don't use prod mode
        _JAVA_HOST_: '//localhost:55080',
        _LANG_: lang,
        _EXTERNAL_CHANNEL_SOURCES_: 'BEELINE',
        _OWN_CHANNEL_SOURCE_: 'OWN_CHANNELS'
      }),
      new CopyPlugin([
        {
          from: './img',
          to:   'img'
        },
        {
          from: './fonts',
          to:   'fonts'
        }
      ]),
      new ExtractTextPlugin(`main_${hash}.css`),
      new HtmlPlugin({
        template: 'index.ejs',
        inject:   false,
        minify:   useProdMode && {collapseWhitespace: true},
        _hash:    hash,
        _lang:    lang
      })
    ]
  };

  if (useProdMode) {
    config.entry.main = ['./app/main-aot.ts'];
    config.module.rules.push(
      {
        test: /\.ts$/,
        use: ['@ngtools/webpack', 'L10n-loader']
      },
      {
        test: /\.html$/,
        use: ['raw-loader', 'L10n-loader']
      }
    );
    config.plugins.push(
      new AotPlugin({
        tsConfigPath: './tsconfig-aot.json',
        entryModule: path.resolve(__dirname, 'app/app.module#AppModule')
      }),
      new UglifyJSPlugin({
        comments:  false,
        sourceMap: true
      })
    );
  } else {
    config.entry.main = ['./app/main.ts'];
    config.module.rules.push(
      {
        test: /\.ts$/,
        use: ['ts-loader', 'angular-router-loader', 'angular2-template-loader?keepUrl=true', 'L10n-loader']
      },
      {
        test: /\.html$/,
        use: [{
          loader: 'file-loader',
          options: {
            name: '[name]_[md5:hash:hex:6].[ext]'
          }
        }, 'L10n-loader']
      }
    );
  }
  /*config.plugins.push(
    new webpack.BannerPlugin(banner)
  );*/

  return config;
}
