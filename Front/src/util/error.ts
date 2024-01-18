const {DEV} = import.meta.env;

/**
 * CatchError 데코레이터 함수.
 * 이 함수는 객체의 메소드에 적용되며, 해당 메소드에서 발생하는 모든 예외를 잡아 처리합니다.
 * 예외가 발생하면, 개발 모드(`DEV`)에서는 메소드 이름과 함께 오류 정보를 콘솔에 기록합니다.
 */
function CatchError(_: any, propertyName: string, descriptor: TypedPropertyDescriptor<any>) {
    const method = descriptor.value;

    descriptor.value = async function (...args: any[]) {
        try {
            return await method.apply(this, args);
        } catch (error) {
            if (DEV) console.log(`Error in ${propertyName}:`, error);
        }
    };
}

export {CatchError};