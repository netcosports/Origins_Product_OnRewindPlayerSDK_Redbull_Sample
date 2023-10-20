Pod::Spec.new do |s|  
    s.name = 'onrewindshared'
    s.version = '1.1.21'
    s.summary = 'Summary of onrewindshared'
    s.homepage = 'https://github.com/netcosports'

    s.author = { 'Sergei Mikhan' => 'sergei@netcosports.com' }
    s.license = {
        :type => "Copyright",
        :text => "Copyright 2020 Origins Digital"
    }

    s.platform = :ios
    s.source = { :http => 'https://origins-mobile-products.s3.eu-west-1.amazonaws.com/onrewind_player/redbull/1.1.21/onrewindshared.xcframework.zip' }

    s.ios.deployment_target = '13.0'
    s.ios.vendored_frameworks = 'onrewindshared.xcframework'





end
