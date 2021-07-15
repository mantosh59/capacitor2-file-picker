Pod::Spec.new do |s|
    s.name = 'CapacitorFilePicker'
    s.version = '0.0.9'
    s.summary = 'This plugin presents the native UI for picking a file.'
    s.license = 'MIT'
    s.homepage = 'https://github.com/xelits/capacitor-file-picker'
    s.author = 'XELIT'
    s.source = { :git => 'https://github.com/xelits/capacitor-file-picker', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end
